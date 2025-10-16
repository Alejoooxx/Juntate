package com.example.juntate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.lang.Exception

data class Event(
    val id: String = "",
    val eventName: String = "",
    val eventDate: String = "",
    val eventTime: String = "",
    val eventLocality: String = "",
    val eventNeighborhood: String = "",
    val eventLevel: String = "",
    val eventNotes: String = "",
    val sport: String = "",
    val createdByUid: String = "",
    val createdAt: Timestamp? = null,
    val participants: List<String> = emptyList(),
    val requiredParticipants: Int = 0
)

class EventViewModel : ViewModel() {

    private val db = FirebaseModule.db
    private val auth = FirebaseModule.auth
    private val futbolEventsCollection = db.collection("events")
    private val runningEventsCollection = db.collection("running_events")
    private val gymEventsCollection = db.collection("gym_events")
    private val usersCollection = db.collection("users")

    private val _futbolEvents = MutableStateFlow<List<Event>>(emptyList())
    val futbolEvents: StateFlow<List<Event>> = _futbolEvents
    private val _runningEvents = MutableStateFlow<List<Event>>(emptyList())
    val runningEvents: StateFlow<List<Event>> = _runningEvents
    private val _gymEvents = MutableStateFlow<List<Event>>(emptyList())
    val gymEvents: StateFlow<List<Event>> = _gymEvents

    private val _userEvents = MutableStateFlow<List<Event>>(emptyList())
    val userEvents: StateFlow<List<Event>> = _userEvents

    private val _isUserEventsLoading = MutableStateFlow(true)
    val isUserEventsLoading: StateFlow<Boolean> = _isUserEventsLoading

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent

    private val _participantProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val participantProfiles: StateFlow<List<UserProfile>> = _participantProfiles

    private var futbolFilteredEventsListener: ListenerRegistration? = null
    private var runningFilteredEventsListener: ListenerRegistration? = null
    private var gymFilteredEventsListener: ListenerRegistration? = null
    private var userFutbolEventsListener: ListenerRegistration? = null
    private var userRunningEventsListener: ListenerRegistration? = null
    private var userGymEventsListener: ListenerRegistration? = null
    private var singleEventListener: ListenerRegistration? = null

    private val _tempUserFutbolEvents = MutableStateFlow<List<Event>>(emptyList())
    private val _tempUserRunningEvents = MutableStateFlow<List<Event>>(emptyList())
    private val _tempUserGymEvents = MutableStateFlow<List<Event>>(emptyList())

    init {
        viewModelScope.launch {
            combine(
                _tempUserFutbolEvents,
                _tempUserRunningEvents,
                _tempUserGymEvents
            ) { futbol, running, gym ->
                (futbol + running + gym)
                    .sortedByDescending { it.createdAt ?: Timestamp(Date(0)) }
                    .distinctBy { it.id }
            }.collect { combinedList ->
                _userEvents.value = combinedList
                if (_isUserEventsLoading.value) {
                    _isUserEventsLoading.value = false
                    Log.d("EventViewModel", "Carga inicial de UserEvents completa.")
                }
                Log.d("EventViewModel", "UserEvents actualizado con ${combinedList.size} eventos.")
            }
        }
    }

    fun joinEvent(eventId: String, sportType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("Debes iniciar sesión para unirte.")
                return@launch
            }
            val collection = when {
                sportType.equals("Running", ignoreCase = true) -> runningEventsCollection
                sportType.equals("Gym", ignoreCase = true) -> gymEventsCollection
                else -> futbolEventsCollection
            }
            try {
                collection.document(eventId).update("participants", FieldValue.arrayUnion(currentUser.uid)).await()
                Log.d("EventViewModel", "Usuario ${currentUser.uid} unido al evento $eventId en $sportType")
                onSuccess()
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error al unirse al evento $eventId en $sportType", e)
                onError(e.message ?: "Error desconocido al unirse.")
            }
        }
    }

    fun listenForEvents(userLocation: String?, currentUserUid: String?, sportType: String) {
        val listenerToUse: ListenerRegistration?
        val stateFlowToUpdate: MutableStateFlow<List<Event>>
        val collectionToQuery: com.google.firebase.firestore.CollectionReference
        var listenerRef: ListenerRegistration?

        when {
            sportType.equals("Running", ignoreCase = true) -> {
                listenerToUse = runningFilteredEventsListener
                stateFlowToUpdate = _runningEvents
                collectionToQuery = runningEventsCollection
            }
            sportType.equals("Gym", ignoreCase = true) -> {
                listenerToUse = gymFilteredEventsListener
                stateFlowToUpdate = _gymEvents
                collectionToQuery = gymEventsCollection
            }
            else -> {
                listenerToUse = futbolFilteredEventsListener
                stateFlowToUpdate = _futbolEvents
                collectionToQuery = futbolEventsCollection
            }
        }

        listenerToUse?.remove()

        if (currentUserUid == null) {
            stateFlowToUpdate.value = emptyList()
            Log.w("EventViewModel", "No hay UID, no se pueden cargar eventos para $sportType.")
            return
        }

        val query = collectionToQuery.orderBy("createdAt", Query.Direction.DESCENDING)
        Log.d("EventViewModel", "Iniciando listener FILTRADO para $sportType.")

        listenerRef = query.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("EventViewModel", "Error escuchando eventos FILTRADOS de $sportType", error)
                stateFlowToUpdate.value = emptyList()
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val allEvents = snapshots.documents.mapNotNull { doc -> doc.toObject(Event::class.java)?.copy(id = doc.id) }
                val filteredEvents = allEvents.filter { event ->
                    val isCreatedByUser = event.createdByUid == currentUserUid
                    val matchesProfileLocation = userLocation == null || userLocation.isBlank() || userLocation.equals("Bogotá D.C.", ignoreCase = true) || event.eventLocality.equals(userLocation, ignoreCase = true)
                    isCreatedByUser || matchesProfileLocation
                }.distinctBy { it.id }
                stateFlowToUpdate.value = filteredEvents
                Log.d("EventViewModel", "[$sportType FILTRADO] Eventos filtrados: ${filteredEvents.size}.")
            } else {
                stateFlowToUpdate.value = emptyList()
                Log.w("EventViewModel", "Snapshot nulo FILTRADO para $sportType.")
            }
        }

        when {
            sportType.equals("Running", ignoreCase = true) -> runningFilteredEventsListener = listenerRef
            sportType.equals("Gym", ignoreCase = true) -> gymFilteredEventsListener = listenerRef
            else -> futbolFilteredEventsListener = listenerRef
        }
    }

    fun listenForUserEvents(currentUserUid: String?) {
        userFutbolEventsListener?.remove()
        userRunningEventsListener?.remove()
        userGymEventsListener?.remove()

        _isUserEventsLoading.value = true

        if (currentUserUid == null) {
            _tempUserFutbolEvents.value = emptyList()
            _tempUserRunningEvents.value = emptyList()
            _tempUserGymEvents.value = emptyList()
            _isUserEventsLoading.value = false
            Log.w("EventViewModel", "No hay UID, limpiando eventos del usuario.")
            return
        }

        Log.d("EventViewModel", "Iniciando listeners para eventos del usuario: $currentUserUid")

        userFutbolEventsListener = futbolEventsCollection
            .whereArrayContains("participants", currentUserUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("EventViewModel", "Error escuchando eventos de FÚTBOL del usuario", error)
                    _tempUserFutbolEvents.value = emptyList()
                    return@addSnapshotListener
                }
                _tempUserFutbolEvents.value = snapshots?.documents?.mapNotNull {
                    it.toObject(Event::class.java)?.copy(id = it.id)
                } ?: emptyList()
                Log.d("EventViewModel", "Recibidos ${_tempUserFutbolEvents.value.size} eventos de FÚTBOL del usuario.")
            }

        userRunningEventsListener = runningEventsCollection
            .whereArrayContains("participants", currentUserUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("EventViewModel", "Error escuchando eventos de RUNNING del usuario", error)
                    _tempUserRunningEvents.value = emptyList()
                    return@addSnapshotListener
                }
                _tempUserRunningEvents.value = snapshots?.documents?.mapNotNull {
                    it.toObject(Event::class.java)?.copy(id = it.id)
                } ?: emptyList()
                Log.d("EventViewModel", "Recibidos ${_tempUserRunningEvents.value.size} eventos de RUNNING del usuario.")
            }

        userGymEventsListener = gymEventsCollection
            .whereArrayContains("participants", currentUserUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("EventViewModel", "Error escuchando eventos de GYM del usuario", error)
                    _tempUserGymEvents.value = emptyList()
                    return@addSnapshotListener
                }
                _tempUserGymEvents.value = snapshots?.documents?.mapNotNull {
                    it.toObject(Event::class.java)?.copy(id = it.id)
                } ?: emptyList()
                Log.d("EventViewModel", "Recibidos ${_tempUserGymEvents.value.size} eventos de GYM del usuario.")
            }
    }

    fun fetchParticipantProfiles(participantUids: List<String>) {
        viewModelScope.launch {
            _participantProfiles.value = emptyList()
            val profiles = mutableListOf<UserProfile>()
            try {
                for (uid in participantUids) {
                    val doc = usersCollection.document(uid).get().await()
                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        profiles.add(profile)
                    }
                }
                _participantProfiles.value = profiles
                Log.d("EventViewModel", "Fetched ${profiles.size} participant profiles.")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error fetching participant profiles", e)
            }
        }
    }

    fun leaveEvent(eventId: String, sportType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("Usuario no autenticado.")
                return@launch
            }
            val collection = when {
                sportType.equals("Running", ignoreCase = true) -> runningEventsCollection
                sportType.equals("Gym", ignoreCase = true) -> gymEventsCollection
                else -> futbolEventsCollection
            }
            try {
                collection.document(eventId).update("participants", FieldValue.arrayRemove(currentUser.uid)).await()
                Log.d("EventViewModel", "Usuario ${currentUser.uid} salió del evento $eventId")
                onSuccess()
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error al salir del evento $eventId", e)
                onError(e.message ?: "Error desconocido.")
            }
        }
    }

    fun deleteEvent(eventId: String, sportType: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val collection = when {
                sportType.equals("Running", ignoreCase = true) -> runningEventsCollection
                sportType.equals("Gym", ignoreCase = true) -> gymEventsCollection
                else -> futbolEventsCollection
            }
            try {
                collection.document(eventId).delete().await()
                Log.d("EventViewModel", "Evento $eventId eliminado.")
                onSuccess()
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error al eliminar evento $eventId", e)
                onError(e.message ?: "Error desconocido.")
            }
        }
    }

    fun listenForSingleEventDetails(eventId: String, sportType: String) {
        singleEventListener?.remove()
        _selectedEvent.value = null

        Log.d("EventViewModel", "Iniciando listener para evento único: $eventId en $sportType")

        val collection = when {
            sportType.equals("Running", ignoreCase = true) -> runningEventsCollection
            sportType.equals("Gym", ignoreCase = true) -> gymEventsCollection
            else -> futbolEventsCollection
        }

        singleEventListener = collection.document(eventId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("EventViewModel", "Error escuchando evento único $eventId", error)
                _selectedEvent.value = null
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                _selectedEvent.value = snapshot.toObject(Event::class.java)?.copy(id = snapshot.id)
                Log.d("EventViewModel", "Detalles de evento $eventId cargados.")
            } else {
                Log.w("EventViewModel", "No se encontró el evento $eventId.")
                _selectedEvent.value = null
            }
        }
    }

    fun clearSingleEventListener() {
        singleEventListener?.remove()
        _selectedEvent.value = null
        _participantProfiles.value = emptyList()
    }


    fun createEvent(
        eventName: String, eventDate: String, eventTime: String, eventLocality: String,
        eventNeighborhood: String, eventLevel: String, eventNotes: String,
        requiredParticipants: Int, sportType: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("Usuario no autenticado.")
                return@launch
            }
            val collection = when {
                sportType.equals("Running", ignoreCase = true) -> runningEventsCollection
                sportType.equals("Gym", ignoreCase = true) -> gymEventsCollection
                else -> futbolEventsCollection
            }
            val newEvent = hashMapOf(
                "eventName" to eventName,
                "eventDate" to eventDate,
                "eventTime" to eventTime,
                "eventLocality" to eventLocality,
                "eventNeighborhood" to eventNeighborhood,
                "eventLevel" to eventLevel,
                "eventNotes" to eventNotes,
                "sport" to sportType,
                "createdByUid" to currentUser.uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "participants" to listOf(currentUser.uid),
                "requiredParticipants" to requiredParticipants
            )
            try {
                collection.add(newEvent).await()
                onSuccess()
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error creando evento de $sportType", e)
                onError(e.message ?: "Error desconocido.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        futbolFilteredEventsListener?.remove()
        runningFilteredEventsListener?.remove()
        gymFilteredEventsListener?.remove()
        userFutbolEventsListener?.remove()
        userRunningEventsListener?.remove()
        userGymEventsListener?.remove()
        singleEventListener?.remove()
        Log.d("EventViewModel", "Todos los listeners de eventos removidos.")
    }
}