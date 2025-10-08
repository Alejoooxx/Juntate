package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.juntate.R

val JuntateGreen = Color(0xFF1E8C83)
val JuntateBackground = Color(0xFF166660)
val LightGray = Color(0xFFF3F3F3)
val MediumGray = Color(0xFFBDBDBD)
val TextGray = Color(0xFF8A8A8A)
val White = Color.White
val Black = Color.Black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val cornerImageSize = 350.dp
    val imageOffset = cornerImageSize / 3

    // Contenedor Principal
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(JuntateBackground, JuntateGreen)
                )
            )
    ) {
        // Imagen de fondo superior
        Image(
            painter = painterResource(id = R.drawable.esquina1),
            contentDescription = null,
            modifier = Modifier
                .size(cornerImageSize)
                .align(Alignment.TopStart)
                .offset(x = -imageOffset, y = -imageOffset)
                .alpha(0.15f)
        )

        // Imagen de fondo inferior
        Image(
            painter = painterResource(id = R.drawable.esquina2),
            contentDescription = null,
            modifier = Modifier
                .size(cornerImageSize)
                .align(Alignment.BottomEnd)
                .offset(x = imageOffset, y = imageOffset)
                .alpha(0.15f)
        )

        // Columna para centrar la tarjeta de login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tarjeta Blanca
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoverde),
                        contentDescription = "Logo de Juntate",
                        modifier = Modifier.size(90.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Iniciar Sesión
                    Text(
                        text = "Iniciar Sesión",
                        color = Black,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Correo electrónico", color = MediumGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = LightGray,
                            focusedContainerColor = LightGray,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = JuntateGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Contraseña", color = MediumGray) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = LightGray,
                            focusedContainerColor = LightGray,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = JuntateGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Olvidaste tu contraseña
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Iniciar sesión
                    Button(
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(containerColor = JuntateGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Texto de registro
                    Row(
                        modifier = Modifier.clickable { onRegisterClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Regístrate",
                            color = JuntateGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}