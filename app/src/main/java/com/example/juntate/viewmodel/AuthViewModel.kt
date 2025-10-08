package com.example.juntate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juntate.data.FirebaseModule
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseModule.auth
    private val db = FirebaseModule.db

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email
                    )
                    db.collection("users").document(uid).set(userData)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it.message ?: "Error al guardar datos") }
                }
                .addOnFailureListener {
                    onError(it.message ?: "Error al registrar usuario")
                }
        }
    }
}
