package com.example.juntate.ui.theme.screens


import com.example.juntate.ui.theme.screens.BottomNavigationBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.juntate.R
import com.example.juntate.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutbolScreen(navController: NavHostController) {
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
                // navigationIcon removed
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen
                )
            )
        },
        bottomBar = {
            // Ensure this function is defined/imported correctly
            BottomNavigationBar(navController = navController, currentScreen = "futbol_screen")
        },
        floatingActionButton = {
            FloatingActionButton(
                // ✅ ACTION: Navigate to the create event screen route
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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EventCardFutbol(
                    title = stringResource(id = R.string.sample_event_title_1),
                    dateTime = stringResource(id = R.string.sample_event_datetime_1),
                    location = stringResource(id = R.string.sample_event_location_1),
                    isFull = false,
                    onJoinClick = { /* TODO */ }
                )
            }
            item {
                EventCardFutbol(
                    title = stringResource(id = R.string.sample_event_title_2),
                    dateTime = stringResource(id = R.string.sample_event_datetime_2),
                    location = stringResource(id = R.string.sample_event_location_2),
                    isFull = true,
                    onJoinClick = { /* Disabled */ }
                )
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
                Button(
                    onClick = onJoinClick,
                    enabled = !isFull,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFull) Color.Gray else PrimaryGreen,
                        contentColor = Color.White,
                        disabledContainerColor = MediumGray,
                        disabledContentColor = TextGray
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 18.dp)
                ) {
                    Text(
                        text = if (isFull) stringResource(id = R.string.event_card_complete_button) else stringResource(id = R.string.event_card_join_button),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun FutbolScreenPhonePreview() {
    JuntateTheme {
        FutbolScreen(navController = rememberNavController())
    }
}
