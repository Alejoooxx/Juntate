package com.example.juntate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

// ⛔️ Ya no necesitamos AuthState ni StateFlow. Haremos todo con callbacks.

data class UserProfile(
    val name: String = "",
    val email: String = ""
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseModule.auth
    private val db = FirebaseModule.db

    // (El StateFlow del perfil se mantiene para ProfileScreen)
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    // ✅ FUNCIÓN DE REGISTRO CORREGIDA Y SIMPLIFICADA
    fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
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
                // Se verifica el tipo de error para dar un mensaje específico
                val errorMessage = if (e is FirebaseAuthUserCollisionException) {
                    "Ya existe una cuenta con ese correo."
                } else {
                    e.message ?: "Error desconocido al registrar."
                }
                onError(errorMessage)
            }
    }

    // ✅ FUNCIÓN DE LOGIN CORREGIDA PARA SER MÁS EXPLÍCITA
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess() // Se llama al callback de éxito
            }
            .addOnFailureListener { e ->
                // Se traduce el error para el usuario
                onError("Correo o contraseña incorrectos.")
            }
    }

    // (La función de fetchCurrentUserProfile se mantiene igual)
    fun fetchCurrentUserProfile() {
        // ... (código sin cambios)
    }
}