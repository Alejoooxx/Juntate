package com.example.juntate.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {

    val viewModel: AuthViewModel = viewModel()
    val userProfile by viewModel.userProfile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUserProfile()
    }

    Scaffold(
        topBar = { ProfileTopAppBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(White, LightGray)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Foto de Perfil
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_profile_placeholder),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, PrimaryGreen, CircleShape)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Información del Usuario
            item { ProfileInfoCard(label = "Nombre Completo", value = userProfile?.name ?: "Cargando...") }
            item { ProfileInfoCard(label = "Correo electrónico", value = userProfile?.email ?: "Cargando...") }
            item { ProfileInfoCard(label = "Fecha de nacimiento", value = "") }
            item { ProfileInfoCard(label = "Ubicación", value = "Bogota D.C") }

            // Separador
            item {
                Divider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    color = MediumGray
                )
            }

            // Secciones de Deportes
            item { SportPreferenceSection(sportName = "Fútbol") }
            item { SportPreferenceSection(sportName = "Running") }
            item { SportPreferenceSection(sportName = "Gym") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar() {
    TopAppBar(
        title = {
            TextButton(onClick = { /* Cerrar sesión */ }) {
                Text("Cerrar sesión", color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            TextButton(onClick = { /* Entrar en modo edición */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Editar Perfil", color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun ProfileInfoCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .background(MutedGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = PrimaryGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = PrimaryGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportPreferenceSection(sportName: String) {
    var selectedLevel by remember { mutableStateOf("Principiante") }
    val levels = listOf("Principiante", "Intermedio", "Avanzado")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = sportName,
            color = PrimaryGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            levels.forEach { level ->
                val isSelected = (selectedLevel == level)

                val border = if (isSelected) {
                    null
                } else {
                    BorderStroke(1.dp, PrimaryGreen)
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedLevel = level },
                    label = { Text(level) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MutedGreen,
                        labelColor = PrimaryGreen,
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = White
                    ),
                    border = border
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    JuntateTheme {
        ProfileScreen()
    }
}