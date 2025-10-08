package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.ColorFilter
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

val BackgroundGray = Color(0xFFF7F7F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val viewModel: AuthViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val cornerImageSize = 350.dp
    val imageOffset = cornerImageSize / 3.5f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(White, BackgroundGray)
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.esquina3),
            contentDescription = null,
            colorFilter = ColorFilter.tint(JuntateGreen),
            modifier = Modifier
                .size(cornerImageSize)
                .align(Alignment.TopStart)
                .offset(x = -imageOffset, y = -imageOffset)
                .alpha(0.35f)
        )

        Image(
            painter = painterResource(id = R.drawable.esquina4),
            contentDescription = null,
            colorFilter = ColorFilter.tint(JuntateGreen),
            modifier = Modifier
                .size(cornerImageSize)
                .align(Alignment.BottomEnd)
                .offset(x = imageOffset, y = imageOffset)
                .alpha(0.35f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = JuntateBackground),
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
                        colorFilter = ColorFilter.tint(White),
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Regístrate",
                        color = White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AuthInputField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Nombre completo",
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AuthInputField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Correo electrónico",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AuthInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Contraseña",
                        isPassword = true,
                        keyboardType = KeyboardType.Password
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "O regístrate con",
                        color = White,
                        fontSize = 14.sp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SocialButton(icon = R.drawable.google, text = "Google")
                        SocialButton(icon = R.drawable.facebook, text = "Facebook")
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SocialButton(icon = R.drawable.instagram, text = "Instagram")
                        SocialButton(icon = R.drawable.apple, text = "Apple")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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
                        colors = ButtonDefaults.buttonColors(containerColor = JuntateGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Crear cuenta",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.clickable { onLoginClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Ya tienes una cuenta? ",
                            color = White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Inicia sesión",
                            color = JuntateGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Bottom))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MediumGray) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = White,
            focusedContainerColor = White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = JuntateGreen
        ),
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 16.sp, color = Black)
    )
}

@Composable
fun SocialButton(icon: Int, text: String) {
    OutlinedButton(
        onClick = { },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = JuntateBackground
        ),
        border = BorderStroke(1.dp, BorderGray),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = Modifier
            .width(140.dp)
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier.width(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = text,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}