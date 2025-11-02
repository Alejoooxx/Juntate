package com.example.juntate.ui.theme.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.juntate.R
import com.example.juntate.ui.theme.*
import com.example.juntate.viewmodel.AuthViewModel
import com.example.juntate.viewmodel.Event
import com.example.juntate.viewmodel.EventViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import com.example.juntate.ui.theme.screens.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutbolScreen(navController: NavHostController) {

    val eventViewModel: EventViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val futbolEvents by eventViewModel.futbolEvents.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val currentUserUid = remember { Firebase.auth.currentUser?.uid }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var profileLoadAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(currentUserUid, userProfile) {
        if (currentUserUid != null) {
            Log.d("FutbolScreen", "LaunchedEffect [profile]: userProfile?.location = ${userProfile?.location}")
            eventViewModel.listenForEvents(userProfile?.location, currentUserUid, "Futbol")
        } else {
            eventViewModel.listenForEvents(null, null, "Futbol")
        }
    }

    LaunchedEffect(currentUserUid) {
        if (currentUserUid != null && userProfile == null && !profileLoadAttempted) {
            Log.d("FutbolScreen", "LaunchedEffect [UID]: Intentando carga inicial del perfil...")
            authViewModel.fetchCurrentUserProfile()
            profileLoadAttempted = true
        } else if (currentUserUid != null && userProfile != null) {
            profileLoadAttempted = true
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.futbol_screen_title),
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "futbol_screen")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("fut_event_screen") },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.create_event_fab_desc)
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(White, TextGray)
                )
            )
    ) { innerPadding ->

        val toastJoined = stringResource(R.string.futbol_join_success_toast)
        val toastErrorTemplate = stringResource(R.string.event_details_toast_error_template)

        when {
            currentUserUid != null && userProfile == null && !profileLoadAttempted -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            currentUserUid != null && futbolEvents.isEmpty() && profileLoadAttempted -> {
                Box(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.futbol_empty_events),
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            futbolEvents.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(futbolEvents) { event ->
                        val isCreator = event.createdByUid == currentUserUid
                        val isParticipant = currentUserUid != null && currentUserUid in event.participants
                        val isFull = event.requiredParticipants > 0 && event.participants.size >= event.requiredParticipants

                        EventCardFutbol(
                            modifier = Modifier,
                            title = event.eventName,
                            dateTime = "${event.eventDate} - ${event.eventTime}",
                            location = "${event.eventNeighborhood}, ${event.eventLocality}",
                            isFull = isFull,
                            isCreator = isCreator,
                            isParticipant = isParticipant,
                            onJoinClick = {
                                if (!isCreator && !isParticipant && currentUserUid != null) {
                                    eventViewModel.joinEvent(
                                        eventId = event.id,
                                        sportType = "Futbol",
                                        onSuccess = {
                                            coroutineScope.launch {
                                                Toast.makeText(context, toastJoined, Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onError = { errorMsg ->
                                            coroutineScope.launch {
                                                Toast.makeText(context, toastErrorTemplate.format(errorMsg), Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
            currentUserUid == null -> {
                Box(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.futbol_login_prompt), color = Color.Gray, fontSize = 18.sp)
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun EventCardFutbol(
    title: String,
    dateTime: String,
    location: String,
    isFull: Boolean,
    isCreator: Boolean,
    isParticipant: Boolean,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = dateTime,
                        color = Color.Black,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(id = R.string.event_card_location_icon_desc),
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = location,
                            color = Color.Black,
                            fontSize = 18.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                val buttonEnabled = !isFull && !isCreator && !isParticipant
                Button(
                    onClick = onJoinClick,
                    enabled = buttonEnabled,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (buttonEnabled) PrimaryGreen else Color.Gray,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 18.dp)
                ) {
                    val buttonText = when {
                        isCreator -> stringResource(R.string.futbol_card_creator_button)
                        isParticipant -> stringResource(R.string.futbol_card_joined_button)
                        isFull -> stringResource(id = R.string.event_card_complete_button)
                        else -> stringResource(id = R.string.event_card_join_button)
                    }
                    Text(
                        text = buttonText,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun FutbolScreenPhonePreview() {
    JuntateTheme {
        FutbolScreen(navController = rememberNavController())
    }
}