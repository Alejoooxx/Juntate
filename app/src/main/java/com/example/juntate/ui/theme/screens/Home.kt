package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.juntate.R
import com.example.juntate.ui.theme.JuntateBackground
import com.example.juntate.ui.theme.PrimaryGreen

val CardShineColor = Color(0x33FFFFFF) // Blanco con baja opacidad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        // Barra de navegación inferior
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding) // Padding automático del Scaffold
                .fillMaxSize()
                .background(JuntateBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            item {
                Text(
                    text = "¿Qué quieres\npracticar hoy?",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }

            // Tarjetas de Deportes
            item {
                SportCard(
                    text = "Fútbol",
                    imageResId = R.drawable.ic_futbol, // Carga de Imagenes
                    imageAlignment = Alignment.CenterStart
                )
            }
            item {
                SportCard(
                    text = "Running",
                    imageResId = R.drawable.ic_running,
                    imageAlignment = Alignment.CenterEnd
                )
            }
            item {
                SportCard(
                    text = "Gym",
                    imageResId = R.drawable.ic_gym,
                    imageAlignment = Alignment.CenterStart
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Espacio final
            }
        }
    }
}

@Composable
fun SportCard(
    text: String,
    imageResId: Int,
    imageAlignment: Alignment = Alignment.CenterStart
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(160.dp) // Altura consistente para todas las tarjetas
            .clickable { /* TODO: Navegar a la pantalla del deporte */ },
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Brillo sutil en la tarjeta
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight()
                    .align(imageAlignment)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(CardShineColor, Color.Transparent),
                            radius = 300f
                        )
                    )
            )

            // Imagen del deportista
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = text,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .align(imageAlignment)
                    .padding(horizontal = 16.dp)
            )

            // Texto del deporte
            Text(
                text = text,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(if (imageAlignment == Alignment.CenterStart) Alignment.CenterEnd else Alignment.CenterStart)
                    .padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = JuntateBackground,
        contentColor = Color.White,
        modifier = Modifier.height(70.dp)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = "Historial",
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.White,
                indicatorColor = PrimaryGreen
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                indicatorColor = PrimaryGreen
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Perfil",
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.White,
                indicatorColor = PrimaryGreen
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}