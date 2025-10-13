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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {

    val viewModel: AuthViewModel = viewModel()
    val userProfile by viewModel.userProfile.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }

    // --- Estados para los campos editables ---
    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedBirthDate by remember { mutableStateOf("") }
    var editedLocation by remember { mutableStateOf("") }

    // --- Estados para guardar el nivel de cada deporte ---
    var futbolLevel by remember { mutableStateOf<String?>(null) }
    var runningLevel by remember { mutableStateOf<String?>(null) }
    var gymLevel by remember { mutableStateOf<String?>(null) }

    // --- Lógica para el DatePicker ---
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Carga los datos iniciales
    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUserProfile()
    }

    // Cuando el perfil carga o entramos en modo edición, actualizamos los campos editables
    LaunchedEffect(userProfile, isEditMode) {
        userProfile?.let {
            if (isEditMode) {
                editedName = it.name
                editedEmail = it.email
                editedBirthDate = it.birthDate
                editedLocation = it.location
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            editedBirthDate = formatter.format(Date(millis))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            ProfileTopAppBar(
                isEditMode = isEditMode,
                onEditClick = { isEditMode = true },
                onSaveClick = {
                    viewModel.updateUserProfile(
                        newName = editedName,
                        newEmail = editedEmail,
                        newBirthDate = editedBirthDate,
                        newLocation = editedLocation,
                        onSuccess = {
                            isEditMode = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Perfil guardado con éxito")
                            }
                        },
                        onError = { error ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(error)
                            }
                        }
                    )
                },
                onCancelClick = { isEditMode = false }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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

            // --- INFORMACIÓN DE USUARIO (CONDICIONAL) ---
            item {
                if (isEditMode) {
                    // MODO EDICIÓN
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = editedEmail, onValueChange = { editedEmail = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = editedBirthDate, onValueChange = { }, label = { Text("Fecha de nacimiento") }, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true })
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = editedLocation, onValueChange = { editedLocation = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    // MODO VISTA
                    Column {
                        ProfileInfoCard(label = "Nombre Completo", value = userProfile?.name ?: "Cargando...")
                        ProfileInfoCard(label = "Correo electrónico", value = userProfile?.email ?: "Cargando...")
                        ProfileInfoCard(label = "Fecha de nacimiento", value = userProfile?.birthDate ?: "")
                        ProfileInfoCard(label = "Ubicación", value = userProfile?.location ?: "")
                    }
                }
            }

            item {
                Divider(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    color = MediumGray
                )
            }

            // --- SECCIÓN DE DEPORTES CONDICIONAL ---
            if (isEditMode) {
                item { SportPreferenceSection(sportName = "Fútbol", currentLevel = futbolLevel, onLevelSelected = { futbolLevel = it }) }
                item { SportPreferenceSection(sportName = "Running", currentLevel = runningLevel, onLevelSelected = { runningLevel = it }) }
                item { SportPreferenceSection(sportName = "Gym", currentLevel = gymLevel, onLevelSelected = { gymLevel = it }) }
            } else {
                item { SportDisplayCard(sportName = "Fútbol", selectedLevel = futbolLevel) }
                item { SportDisplayCard(sportName = "Running", selectedLevel = runningLevel) }
                item { SportDisplayCard(sportName = "Gym", selectedLevel = gymLevel) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(
    isEditMode: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    TopAppBar(
        title = {
            TextButton(onClick = { /* TODO: Lógica de cerrar sesión */ }) {
                Text("Cerrar sesión", color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            if (isEditMode) {
                TextButton(onClick = onSaveClick) { Text("Guardar", fontWeight = FontWeight.Bold) }
                TextButton(onClick = onCancelClick) { Text("Cancelar", fontWeight = FontWeight.Bold) }
            } else {
                TextButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar Perfil", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
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
        Text(text = label, color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SportDisplayCard(sportName: String, selectedLevel: String?) {
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
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                .background(MutedGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedLevel ?: "Escoger experiencia",
                color = if (selectedLevel != null) PrimaryGreen else MediumGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportPreferenceSection(
    sportName: String,
    currentLevel: String?,
    onLevelSelected: (String) -> Unit
) {
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
                val isSelected = (currentLevel == level)
                val border = if (isSelected) null else BorderStroke(1.dp, PrimaryGreen)

                FilterChip(
                    selected = isSelected,
                    onClick = { onLevelSelected(level) },
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