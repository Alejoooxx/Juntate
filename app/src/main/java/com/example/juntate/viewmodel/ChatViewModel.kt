package com.example.juntate.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class Message(
    val id: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val messageText: String? = null,
    val mediaUrl: String? = null,
    val messageType: String = "TEXT",
    val timestamp: Any? = null
)

class ChatViewModel : ViewModel() {

    private val db = FirebaseModule.db
    private val auth = FirebaseModule.auth
    private val storage = Firebase.storage

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private var chatListener: ListenerRegistration? = null

    private fun getChatCollection(sportType: String, eventId: String): CollectionReference {
        val collectionName = when {
            sportType.equals("Running", ignoreCase = true) -> "running_events"
            sportType.equals("Gym", ignoreCase = true) -> "gym_events"
            else -> "events"
        }
        return db.collection(collectionName).document(eventId).collection("messages")
    }

    fun listenForMessages(sportType: String, eventId: String) {
        clearChatListener()
        val collection = getChatCollection(sportType, eventId)

        chatListener = collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("ChatViewModel", "Listen failed.", error)
                    _messages.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    _messages.value = snapshots.documents.mapNotNull {
                        it.toObject(Message::class.java)?.copy(id = it.id)
                    }
                    Log.d("ChatViewModel", "Loaded ${_messages.value.size} messages.")
                } else {
                    _messages.value = emptyList()
                }
            }
    }

    fun sendMessage(
        sportType: String,
        eventId: String,
        messageText: String,
        senderName: String
    ) {
        val currentUser = auth.currentUser ?: return
        val collection = getChatCollection(sportType, eventId)

        val message = Message(
            senderUid = currentUser.uid,
            senderName = senderName,
            messageText = messageText,
            messageType = "TEXT",
            timestamp = FieldValue.serverTimestamp()
        )

        viewModelScope.launch {
            try {
                collection.add(message).await()
                Log.d("ChatViewModel", "Message sent")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
            }
        }
    }

    fun sendMediaMessage(
        sportType: String,
        eventId: String,
        uri: Uri,
        senderName: String,
        messageType: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return
        val collection = getChatCollection(sportType, eventId)
        val fileName = "${UUID.randomUUID()}-${uri.lastPathSegment}"
        val storageRef = storage.reference.child("chat_media/$eventId/$fileName")

        viewModelScope.launch {
            try {
                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

                val message = Message(
                    senderUid = currentUser.uid,
                    senderName = senderName,
                    mediaUrl = downloadUrl,
                    messageType = messageType,
                    timestamp = FieldValue.serverTimestamp()
                )

                collection.add(message).await()
                Log.d("ChatViewModel", "Media message sent")
                onSuccess()
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending media message", e)
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    fun clearChatListener() {
        chatListener?.remove()
        chatListener = null
        _messages.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        clearChatListener()
    }
}