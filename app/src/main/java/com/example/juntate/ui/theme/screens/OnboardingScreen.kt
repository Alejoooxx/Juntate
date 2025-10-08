package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.juntate.R

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {} // ✅ callback que redirige al LoginScreen
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF247C78), Color(0xFF095651))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // 🟢 Logo principal
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_logo),
            contentDescription = "Logo Onboarding",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 5.dp)
        )

        // 📝 Texto centrado
        Text(
            text = "Encuentra tu equipo,\nentrena acompañado.",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .padding(bottom = 35.dp)
        )

        // 🏃‍♀️ Imagen principal
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_people),
            contentDescription = "Personas entrenando",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(220.dp)
                .padding(bottom = 40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // 🟩 Botón inferior con callback funcional
        Button(
            onClick = {
                onStartClick() // ✅ Ejecuta el callback para navegar al LoginScreen
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF41A38E)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(55.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Text(
                text = "Empezar ahora",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        onStartClick = { /* 🔄 Simulación de navegación al LoginScreen */ }
    )
}
