package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit = {}, // Navegar tras registro
    onLoginClick: () -> Unit = {}     // Volver a login
) {
    // 🔹 Campos
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 🔹 ViewModel
    val viewModel: AuthViewModel = viewModel()

    // 🔹 Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 🧱 Fondo blanco con esquinas decorativas
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(White)
    ) {
        val cornerSize = maxWidth * 0.78f
        val overlap = cornerSize * 0.24f

        // 🟢 Esquinas decorativas
        Image(
            painter = painterResource(id = R.drawable.esquina3),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerSize)
                .offset(x = -overlap, y = -overlap),
            contentScale = ContentScale.Fit,
            alpha = 0.25f
        )

        Image(
            painter = painterResource(id = R.drawable.esquina4),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerSize)
                .offset(x = overlap, y = overlap),
            contentScale = ContentScale.Fit,
            alpha = 0.25f
        )

        // 🟩 Tarjeta central
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF095651))
                    .padding(vertical = 36.dp, horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // 🟢 Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_onboarding_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    // 🧾 Título
                    Text(
                        text = "Regístrate",
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // ✏️ Campos
                    InputField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Nombre completo"
                    )

                    InputField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Correo electrónico"
                    )

                    InputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Contraseña",
                        isPassword = true
                    )

                    // 📝 Texto intermedio
                    Text(
                        text = "O regístrate con",
                        color = White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // 🔗 Botones sociales
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        SocialButton(icon = R.drawable.google, text = "Google")
                        SocialButton(icon = R.drawable.facebook, text = "Facebook")
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        SocialButton(icon = R.drawable.instagram, text = "Instagram")
                        SocialButton(icon = R.drawable.apple, text = "Apple")
                    }

                    // 🟩 Botón principal funcional con Firebase
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                                viewModel.registerUser(
                                    name,
                                    email,
                                    password,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Registro exitoso 🎉")
                                        }
                                        onRegisterClick()
                                    },
                                    onError = { error ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(error)
                                        }
                                    }
                                )
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Completa todos los campos.")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "Crear cuenta",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 🔁 Enlace para volver a iniciar sesión
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Ya tienes una cuenta? ",
                            color = White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Inicia sesión",
                            color = AccentPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onLoginClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MediumGray) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(White),
        textStyle = TextStyle(fontSize = 16.sp)
    )
}

@Composable
fun SocialButton(icon: Int, text: String) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(45.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFF8D8D8D), RoundedCornerShape(10.dp))
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .size(22.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = text,
                color = Color(0xFF195E5A),
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
