package com.example.juntate.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.juntate.viewmodel.Event
import com.example.juntate.viewmodel.EventViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventHistoryScreen(navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val eventViewModel: EventViewModel = viewModel()
    val userEvents by eventViewModel.userEvents.collectAsState()
    val isLoading by eventViewModel.isUserEventsLoading.collectAsState()
    val currentUserUid = remember { Firebase.auth.currentUser?.uid }

    LaunchedEffect(currentUserUid) {
        if (currentUserUid != null) {
            Log.d("EventHistoryScreen", "Iniciando escucha para eventos del usuario: $currentUserUid")
            eventViewModel.listenForUserEvents(currentUserUid)
        } else {
            eventViewModel.listenForUserEvents(null)
            Log.d("EventHistoryScreen", "No hay usuario, limpiando eventos.")
        }
    }

    Scaffold(
        topBar = {
            HistoryHeader(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "history")
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
        when {
            currentUserUid != null && isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Log.d("EventHistoryScreen", "Mostrando indicador de carga...")
                }
            }
            currentUserUid != null && !isLoading && userEvents.isEmpty() -> {
                Box(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No te has unido a ningún evento aún.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center, color = MediumGray, fontSize = 18.sp
                    )
                    Log.d("EventHistoryScreen", "Mostrando mensaje 'Sin eventos'.")
                }
            }
            userEvents.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val itemsToShow = if (selectedTabIndex == 0) {
                        userEvents
                    } else {
                        emptyList<Event>()
                    }

                    if (itemsToShow.isNotEmpty()) {
                        items(itemsToShow) { event ->
                            HistoryEventCard(
                                navController = navController,
                                title = event.eventName,
                                dateTime = "${event.eventDate} - ${event.eventTime}",
                                location = "${event.eventNeighborhood}, ${event.eventLocality}",
                                eventId = event.id,
                                sportType = event.sport,
                                isPastEvent = selectedTabIndex == 1,
                                showStatusText = false
                            )
                        }
                    } else if (selectedTabIndex == 1) {
                        item {
                            Text(
                                text = "Aún no tienes eventos pasados.",
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MediumGray,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
            currentUserUid == null -> {
                Box(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Inicia sesión para ver tu historial", color = Color.Gray, fontSize = 18.sp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryHeader(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreen)
            .padding(top = 16.dp)
            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
    ) {
        Text(
            text = stringResource(id = R.string.history_screen_title),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = PrimaryGreen,
            contentColor = Color.White,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty() && selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = Color.White
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text(text = stringResource(id = R.string.history_tab_current), fontSize = 16.sp) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text(text = stringResource(id = R.string.history_tab_past), fontSize = 16.sp) }
            )
        }
    }
}

@Composable
fun HistoryEventCard(
    navController: NavHostController,
    title: String,
    dateTime: String,
    location: String,
    eventId: String,
    sportType: String,
    isPastEvent: Boolean,
    showStatusText: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = dateTime,
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(id = R.string.event_card_location_icon_desc),
                        tint = PrimaryGreen,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = location,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (showStatusText && !isPastEvent) {
                Text(
                    text = stringResource(id = R.string.history_card_status_upcoming),
                    color = MediumGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            } else if (!isPastEvent) {
                Button(
                    onClick = { navController.navigate("event_details/$sportType/$eventId") },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.history_card_details_button),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun EventHistoryScreenPreview() {
    JuntateTheme {
        EventHistoryScreen(navController = rememberNavController())
    }
}

