package com.example.juntate.ui.theme.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.juntate.R
import com.example.juntate.ui.theme.*

data class Sport(
    @StringRes val nameResId: Int,
    val imageResId: Int,
    val imageAlignment: Alignment
)

val sportsList = listOf(
    Sport(R.string.sport_soccer, R.drawable.ic_futbol, Alignment.CenterStart),
    Sport(R.string.sport_running, R.drawable.ic_running, Alignment.CenterEnd),
    Sport(R.string.sport_gym, R.drawable.ic_gym, Alignment.CenterStart)
)

val CardShineColor = Color(0x33FFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { Header() },
        bottomBar = { BottomNavigationBar(navController = navController, currentScreen = "home") },
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(White, TextGray)
                )
            )
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (maxWidth < 600.dp) {
                PhoneLayout(navController = navController)
            } else {
                TabletLayout(navController = navController)
            }
        }
    }
}

@Composable
fun PhoneLayout(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sportsList) { sport ->
            SportCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 24.dp),
                nameResId = sport.nameResId,
                imageResId = sport.imageResId,
                imageAlignment = sport.imageAlignment,
                cardColor = PrimaryGreen,
                navController = navController
            )
        }
    }
}

@Composable
fun TabletLayout(navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sportsList) { sport ->
            SportCard(
                modifier = Modifier.height(220.dp),
                nameResId = sport.nameResId,
                imageResId = sport.imageResId,
                imageAlignment = sport.imageAlignment,
                cardColor = PrimaryGreen,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.home_header_title),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryGreen
        )
    )
}

@Composable
fun SportCard(
    modifier: Modifier = Modifier,
    @StringRes nameResId: Int,
    imageResId: Int,
    cardColor: Color,
    imageAlignment: Alignment = Alignment.CenterStart,
    navController: NavHostController
) {
    val text = stringResource(id = nameResId)
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.clickable {
            when (nameResId) {
                R.string.sport_soccer -> navController.navigate("futbol_screen")
                R.string.sport_running -> navController.navigate("running_screen")
                R.string.sport_gym -> navController.navigate("gym_screen")
            }
        },
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
                        x = if (imageAlignment == Alignment.CenterStart) (-40).dp else 40.dp
                    )
            )
            val textAlignment = if (imageAlignment == Alignment.CenterStart) {
                BiasAlignment(horizontalBias = 0.5f, verticalBias = 0f)
            } else {
                BiasAlignment(horizontalBias = -0.5f, verticalBias = 0f)
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(textAlignment)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentScreen: String) {
    NavigationBar(
        containerColor = PrimaryGreen,
        contentColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = currentScreen == "history",
            onClick = {
                if (currentScreen != "history") {
                    navController.navigate("history") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_book),
                    contentDescription = stringResource(id = R.string.bottom_nav_history),
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    text = "Mis Eventos",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = PrimaryLightGreen
            )
        )
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = {
                if (currentScreen != "home") {
                    navController.navigate("home"){
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = stringResource(id = R.string.bottom_nav_home),
                    modifier = Modifier.size(38.dp)
                )
            },
            label = {
                Text(
                    text = "Inicio",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = PrimaryLightGreen
            )
        )
        NavigationBarItem(
            selected = currentScreen == "profile",
            onClick = {
                if (currentScreen != "profile") {
                    navController.navigate("profile"){
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = stringResource(id = R.string.bottom_nav_profile),
                    modifier = Modifier.size(38.dp)
                )
            },
            label = {
                Text(
                    text = "Mi Perfil",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = PrimaryLightGreen
            )
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPhonePreview() {
    MaterialTheme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun HomeScreenTabletPreview() {
    MaterialTheme {
        HomeScreen(navController = rememberNavController())
    }
}
