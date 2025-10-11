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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.juntate.R
import com.example.juntate.ui.theme.*

// Color para el brillo sutil de las tarjetas
val CardShineColor = Color(0x33FFFFFF) // Blanco con baja opacidad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(White, TextGray)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // --- Encabezado Verde ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryGreen)
                        .padding(top = 10.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Qué quieres practicar \nhoy?",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )
                }
            }

            // --- Tarjetas de Deportes ---
            item {
                SportCard(
                    text = "Fútbol",
                    imageResId = R.drawable.ic_futbol,
                    imageAlignment = Alignment.CenterStart,
                    cardColor = PrimaryGreen
                )
            }
            item {
                SportCard(
                    text = "Running",
                    imageResId = R.drawable.ic_running,
                    imageAlignment = Alignment.CenterEnd,
                    cardColor = PrimaryGreen
                )
            }
            item {
                SportCard(
                    text = "Gym",
                    imageResId = R.drawable.ic_gym,
                    imageAlignment = Alignment.CenterStart,
                    cardColor = PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun SportCard(
    text: String,
    imageResId: Int,
    cardColor: Color,
    imageAlignment: Alignment = Alignment.CenterStart
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(190.dp)
            .clickable { /* TODO: Navegar a la pantalla del deporte */ },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .align(imageAlignment)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(CardShineColor, Color.Transparent),
                            radius = 400f
                        )
                    )
            )

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = text,
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(0.7f)
                    .align(imageAlignment)
                    .offset(
                        x = if (imageAlignment == Alignment.CenterStart) (-20).dp else 20.dp
                    )
            )

            // ✅ ÚNICO CAMBIO: Se ajusta la alineación del texto
            val textAlignment = if (imageAlignment == Alignment.CenterStart) {
                // Si la imagen está a la izquierda, el texto se alinea un 50% hacia la derecha del centro.
                BiasAlignment(horizontalBias = 0.5f, verticalBias = 0f)
            } else {
                // Si la imagen está a la derecha, el texto se alinea un 50% hacia la izquierda del centro.
                BiasAlignment(horizontalBias = -0.5f, verticalBias = 0f)
            }

            Text(
                text = text,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(textAlignment)
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = PrimaryGreen,
        contentColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = "History",
                    modifier = Modifier.size(32.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.White,
                indicatorColor = PrimaryLightGreen
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(42.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                indicatorColor = PrimaryLightGreen
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(42.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.White,
                indicatorColor = PrimaryLightGreen
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