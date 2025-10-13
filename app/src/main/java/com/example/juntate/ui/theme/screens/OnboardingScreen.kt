package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.juntate.ui.theme.JuntateTheme
import com.example.juntate.ui.theme.PrimaryGreen
import com.example.juntate.ui.theme.PrimaryLightGreen

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {}
) {
    val darkerGreen = Color(0xFF166660)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryGreen, darkerGreen)
                )
            )
    ) {
        if (maxHeight > maxWidth) {
            PortraitLayout(onStartClick = onStartClick)
        } else {
            LandscapeLayout(onStartClick = onStartClick)
        }
    }
}

@Composable
fun PortraitLayout(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp), // Padding general para dar aire arriba y abajo
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Sección Superior (Logo y Texto) ---
        // Esta sección ahora ocupa el espacio que necesita, sin weight.
        HeaderContent()

        // --- Sección Central (Ilustración) ---
        // La imagen usa weight(1f) para ocupar todo el espacio sobrante.
        // Esto la fuerza a encogerse en pantallas más cortas.
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_people),
            contentDescription = "Personas entrenando",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // ✅ ESTA ES LA CLAVE DE LA SOLUCIÓN
                .padding(vertical = 24.dp)
        )

        // --- Sección Inferior (Botón) ---
        // Esta sección ocupa el espacio que necesita al final.
        StartButton(onClick = onStartClick)
    }
}

@Composable
fun LandscapeLayout(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HeaderContent()
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.ic_onboarding_people),
                    contentDescription = "Personas entrenando",
                    modifier = Modifier.fillMaxHeight(0.8f)
                )
            }
        }
        StartButton(onClick = onStartClick)
    }
}

@Composable
fun HeaderContent() {
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

@Composable
fun StartButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
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


// --- Previews ---
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", name = "Phone Portrait Preview")
@Composable
fun OnboardingScreenPhonePreview() {
    JuntateTheme {
        OnboardingScreen()
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240", name = "Tablet Landscape Preview")
@Composable
fun OnboardingScreenTabletPreview() {
    JuntateTheme {
        OnboardingScreen()
    }
}