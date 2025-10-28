package com.example.juntate.ui.theme.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.Event
import com.example.juntate.viewmodel.EventViewModel
import com.example.juntate.viewmodel.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    navController: NavHostController,
    sportType: String,
    eventId: String
) {
    val eventViewModel: EventViewModel = viewModel()
    val selectedEvent by eventViewModel.selectedEvent.collectAsState()
    val participantProfiles by eventViewModel.participantProfiles.collectAsState()
    val currentUserUid = remember { Firebase.auth.currentUser?.uid }

    LaunchedEffect(eventId, sportType) {
        Log.d("EventDetailsScreen", "Iniciando escucha para evento: $eventId, tipo: $sportType")
        eventViewModel.listenForSingleEventDetails(eventId, sportType)
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { event ->
            if (event.participants.isNotEmpty()) {
                eventViewModel.fetchParticipantProfiles(event.participants)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("EventDetailsScreen", "Limpiando listener de evento único.")
            eventViewModel.clearSingleEventListener()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_button_description), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "event_details")
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(White, TextGray))
            )
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                selectedEvent == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    EventDetailsContent(
                        event = selectedEvent!!,
                        navController = navController,
                        eventViewModel = eventViewModel,
                        currentUserUid = currentUserUid,
                        participantProfiles = participantProfiles
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailsContent(
    event: Event,
    navController: NavHostController,
    eventViewModel: EventViewModel,
    currentUserUid: String?,
    participantProfiles: List<UserProfile>
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        item {
            EventInfoCardDetails(event = event)
        }

        item {
            Text(
                text = "Participantes (${event.participants.size} / ${event.requiredParticipants})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
            )
        }

        if (participantProfiles.isEmpty() && event.participants.isNotEmpty()) {
            item { CircularProgressIndicator(modifier = Modifier.padding(horizontal = 24.dp)) }
        } else if (participantProfiles.isEmpty()) {
            item { Text("Aún no hay participantes.", color = MediumGray, modifier = Modifier.padding(horizontal = 24.dp)) }
        } else {
            items(participantProfiles, key = { profile -> profile.uid }) { profile ->
                ParticipantRow(
                    profile = profile,
                    navController = navController
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                val isCreator = event.createdByUid == currentUserUid
                val isParticipant = currentUserUid != null && currentUserUid in event.participants
                val isFull = event.requiredParticipants > 0 && event.participants.size >= event.requiredParticipants

                if(isCreator || isParticipant) {
                    Button(onClick = { /* Chat */ }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) {
                        Text("Chat grupal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                when {
                    isCreator -> {
                        Button(
                            onClick = {
                                eventViewModel.deleteEvent(event.id, event.sport,
                                    onSuccess = {
                                        Log.d("EventDetails", "Evento eliminado")
                                        Toast.makeText(context, "Evento eliminado", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onError = { errorMsg ->
                                        coroutineScope.launch {
                                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Eliminar Evento", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    isParticipant -> {
                        Button(
                            onClick = {
                                eventViewModel.leaveEvent(event.id, event.sport,
                                    onSuccess = {
                                        Log.d("EventDetails", "Usuario salió del evento")
                                        Toast.makeText(context, "Has salido del evento", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { errorMsg ->
                                        coroutineScope.launch { Toast.makeText(context, "Error al salir: $errorMsg", Toast.LENGTH_LONG).show() }
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Salirse del Evento", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    isFull -> {
                        Button(onClick = {}, enabled = false, colors = ButtonDefaults.buttonColors(disabledContainerColor = MediumGray), modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                            Text("Evento Lleno", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Button(onClick = {
                            eventViewModel.joinEvent(event.id, event.sport,
                                onSuccess = {
                                    Log.d("EventDetails", "Usuario se unió al evento")
                                    Toast.makeText(context, "Te has unido al evento", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMsg ->
                                    coroutineScope.launch {
                                        Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                            Text("Unirse al Evento", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

    }
}


@Composable
fun EventInfoCardDetails(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.eventName,
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            DetailInfoRowCard(icon = Icons.Default.CalendarToday, label = "Fecha", value = event.eventDate)
            DetailInfoRowCard(icon = Icons.Default.Schedule, label = "Hora", value = event.eventTime)
            Spacer(modifier = Modifier.height(12.dp))
            DetailInfoRowCard(icon = Icons.Default.LocationOn, label = "Localidad", value = event.eventLocality)
            DetailInfoRowCard(icon = Icons.Default.HomeWork, label = "Barrio", value = event.eventNeighborhood)
            Spacer(modifier = Modifier.height(12.dp))
            DetailInfoRowCard(icon = Icons.Default.Diamond, label = "Nivel", value = event.eventLevel)
            if (event.eventNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailInfoRowCard(icon = Icons.Default.Notes, label = "Notas Adicionales", value = event.eventNotes)
            }
        }
    }
}


@Composable
fun DetailInfoRowCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryGreen,
            modifier = Modifier.padding(end = 16.dp).size(20.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MediumGray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun ParticipantRow(profile: UserProfile, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = profile.profilePictureUrl.ifBlank { R.drawable.ic_profile_placeholder },
                    contentDescription = profile.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                    error = painterResource(id = R.drawable.ic_profile_placeholder)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            var menuExpanded by remember { mutableStateOf(false) }

            Box {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = stringResource(R.string.icon_desc_options),
                    tint = MediumGray,
                    modifier = Modifier.clickable { menuExpanded = true }
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Reportar", color = Color.Red) },
                        onClick = {
                            navController.navigate("report_player/${profile.uid}")
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Reportar",
                                tint = Color.Red
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryGreen,
            modifier = Modifier.padding(end = 16.dp).size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MediumGray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsScreenPreview() {
    JuntateTheme {
        EventDetailsScreen(
            navController = rememberNavController(),
            sportType = "Futbol",
            eventId = "sampleId"
        )
    }
}