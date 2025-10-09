package com.example.juntate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

// Clase para representar los diferentes estados de la UI
sealed class AuthState {
    object Idle : AuthState() // Estado inicial
    object Loading : AuthState() // Cargando
    object Success : AuthState() // Éxito
    data class Error(val message: String) : AuthState()
    data class EmailAlreadyExists(val message: String) : AuthState() // Nuevo estado para correo existente
}

class AuthViewModel : ViewModel() {

    private val auth = FirebaseModule.auth
    private val db = FirebaseModule.db

    // Se usa StateFlow para comunicar el estado a la pantalla
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerUser(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("Error al obtener UID")

                val userData = hashMapOf("name" to name, "email" to email)
                db.collection("users").document(uid).set(userData).await()

                _authState.value = AuthState.Success

            } catch (e: Exception) {
                // Se diferencia entre error de correo existente y otros errores
                if (e is FirebaseAuthUserCollisionException) {
                    _authState.value = AuthState.EmailAlreadyExists("Ya existe una cuenta con ese correo.")
                } else {
                    _authState.value = AuthState.Error(e.message ?: "Error desconocido")
                }
            }
        }
    }

    // Función para resetear el estado cuando el usuario modifica el campo de texto
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true -> "Correo o contraseña incorrectos."
                    else -> e.message ?: "Error desconocido al iniciar sesión."
                }
                onError(errorMessage)
            }
        }
    }
}