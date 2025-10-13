package com.example.juntate.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

// Modelo para guardar la información del perfil del usuario
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val birthDate: String = "",
    val location: String = "",
    val profilePictureUrl: String = "",
    val futbolLevel: String? = null,
    val runningLevel: String? = null,
    val gymLevel: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseModule.auth
    private val db = FirebaseModule.db
    private val storage = FirebaseStorage.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private var userProfileListener: ListenerRegistration? = null

    fun signOut() {
        userProfileListener?.remove()
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        userProfileListener?.remove()
    }

    fun registerUser(
        name: String, email: String, password: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    onError("Error al obtener el ID de usuario.")
                    return@addOnSuccessListener
                }
                val userData = UserProfile(uid = uid, name = name, email = email)
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e: Exception -> onError(e.message ?: "Error al guardar datos.") }
            }
            .addOnFailureListener { e: Exception ->
                val errorMessage = if (e is FirebaseAuthUserCollisionException) {
                    "Ya existe una cuenta con ese correo."
                } else {
                    e.message ?: "Error desconocido al registrar."
                }
                onError(errorMessage)
            }
    }

    fun loginUser(
        email: String, password: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("Correo o contraseña incorrectos.") }
    }

    fun fetchCurrentUserProfile() {
        userProfileListener?.remove()

        val firebaseUser = auth.currentUser ?: return
        val userDocRef = db.collection("users").document(firebaseUser.uid)

        userProfileListener = userDocRef.addSnapshotListener { document, error ->
            if (error != null) {
                Log.e("ProfileFetch", "Error al escuchar cambios en el perfil.", error)
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {

                _userProfile.value = document.toObject(UserProfile::class.java)
            } else {

                Log.d("ProfileFix", "Perfil no encontrado para ${firebaseUser.uid}, creando uno nuevo.")

                val newProfile = UserProfile(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: ""
                )

                userDocRef.set(newProfile)
                    .addOnSuccessListener {
                        Log.d("ProfileFix", "Nuevo perfil creado exitosamente en Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileFix", "Error al crear el perfil para el usuario existente.", e)
                    }
            }
        }
    }

    fun uploadProfilePicture(uri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_pictures/$userId")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri: Uri ->
                onSuccess(downloadUri.toString())
            }
            .addOnFailureListener { e: Exception ->
                onError(e.message ?: "Error al subir la imagen.")
            }
    }

    fun updateUserProfile(
        profileData: UserProfile,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                onError("No hay un usuario autenticado.")
                return@launch
            }

            try {
                val successMessage = "Perfil guardado con éxito"
                if (user.email != profileData.email) {
                    user.updateEmail(profileData.email).await()
                }

                db.collection("users").document(user.uid).set(profileData).await()

                onSuccess(successMessage)

            } catch (e: Exception) {
                Log.e("ProfileUpdate", "Error al actualizar el perfil", e)
                onError(e.message ?: "Ocurrió un error desconocido.")
            }
        }
    }
}