package com.example.juntate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
    data class EmailAlreadyExists(val message: String) : AuthState()
}

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val birthDate: String = "",
    val location: String = ""
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseModule.auth
    private val db = FirebaseModule.db

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile


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
                val userData = hashMapOf("name" to name, "email" to email)
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Error al guardar datos.") }
            }
            .addOnFailureListener { e ->
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
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError("Correo o contraseña incorrectos.")
            }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun fetchCurrentUserProfile() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Log.w("ProfileFetch", "No hay usuario actual para obtener el perfil.")
            return
        }

        db.collection("users").document(firebaseUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _userProfile.value = UserProfile(
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        birthDate = document.getString("birthDate") ?: "",
                        location = document.getString("location") ?: ""
                    )
                    Log.i("ProfileFetch", "Perfil cargado exitosamente para ${firebaseUser.uid}")
                } else {
                    Log.w("ProfileFetch", "El documento del usuario no existe.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileFetch", "Error al obtener el perfil del usuario.", exception)
                _userProfile.value = UserProfile(name = "Error", email = "No se pudo cargar")
            }
    }

    fun updateUserProfile(
        newName: String,
        newEmail: String,
        newBirthDate: String,
        newLocation: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                onError("No hay un usuario autenticado.")
                return@launch
            }

            try {
                if (user.email != newEmail) {
                    user.updateEmail(newEmail).await()
                }

                val updatedData = mapOf(
                    "name" to newName,
                    "email" to newEmail,
                    "birthDate" to newBirthDate,
                    "location" to newLocation
                )
                db.collection("users").document(user.uid).update(updatedData).await()

                fetchCurrentUserProfile()
                onSuccess()

            } catch (e: Exception) {
                Log.e("ProfileUpdate", "Error al actualizar el perfil", e)
                onError(e.message ?: "Ocurrió un error desconocido.")
            }
        }
    }
}