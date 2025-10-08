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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.juntate.R
import com.example.juntate.ui.theme.*

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},      // ✅ callback cuando se presiona el botón "Iniciar sesión"
    onRegisterClick: () -> Unit = {}    // ✅ callback cuando se presiona el texto "Regístrate"
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 🧱 Fondo con degradado
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(PrimaryGreen, Color(0xFF095651))
                )
            )
    ) {
        val cornerSize = maxWidth * 0.78f
        val overlap = cornerSize * 0.24f

        // 🌿 Esquina superior izquierda
        Image(
            painter = painterResource(id = R.drawable.esquina1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerSize)
                .offset(x = -overlap, y = -overlap),
            contentScale = ContentScale.Fit,
            alpha = 0.22f
        )

        // 🌿 Esquina inferior derecha
        Image(
            painter = painterResource(id = R.drawable.esquina2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerSize)
                .offset(x = overlap, y = overlap),
            contentScale = ContentScale.Fit,
            alpha = 0.22f
        )

        // 🟩 Contenido principal
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
                    .background(White)
                    .padding(vertical = 40.dp, horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // 🟢 Logo
                    Image(
                        painter = painterResource(id = R.drawable.logoverde),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    // 🧾 Título
                    Text(
                        text = "Iniciar Sesión",
                        color = Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // ✉️ Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Correo electrónico", color = MediumGray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightGray),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    // 🔒 Campo Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Contraseña", color = MediumGray) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightGray),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    // 🔗 Texto auxiliar
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // 🟩 Botón principal → redirige a Home o similar
                    Button(
                        onClick = { onLoginClick() }, // ✅ callback al presionar
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 🟣 Texto de registro → redirige a pantalla Register
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Regístrate",
                            color = AccentPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onRegisterClick() } // ✅ callback funcional
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
    LoginScreen()
}
