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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.juntate.R
import com.example.juntate.ui.theme.PrimaryGreen
import com.example.juntate.ui.theme.PrimaryLightGreen

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {}
) {
    val darkerGreen = Color(0xFF166660)
    val peopleImageHeight = 280.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryGreen, darkerGreen)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Sección Superior (Logo y Texto)
        Column(
            modifier = Modifier
                .weight(1.2f)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoverde),
                contentDescription = "Logo de Juntate",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Encuentra tu equipo,\nentrena acompañado.",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
        }

        // Sección Central (Ilustración)
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_people),
            contentDescription = "Personas entrenando",
            modifier = Modifier
                .fillMaxWidth()
                .height(peopleImageHeight)
                .weight(1.2f, fill = false)
                .padding(vertical = 24.dp)
        )

        // Sección Inferior (Botón)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLightGreen
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(55.dp)
            ) {
                Text(
                    text = "Empezar ahora",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    androidx.compose.material3.MaterialTheme {
        OnboardingScreen()
    }
}