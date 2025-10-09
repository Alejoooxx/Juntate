package com.example.juntate.ui.theme.screens

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
    val name: String,
    val imageResId: Int,
    val imageAlignment: Alignment
)

val sportsList = listOf(
    Sport("Fútbol", R.drawable.ic_futbol, Alignment.CenterStart),
    Sport("Running", R.drawable.ic_running, Alignment.CenterEnd),
    Sport("Gym", R.drawable.ic_gym, Alignment.CenterStart)
)

val CardShineColor = Color(0x33FFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(White, TextGray)
                    )
                )
        ) {
            if (maxWidth < 600.dp) {
                PhoneLayout()
            } else {
                TabletLayout()
            }
        }
    }
}

@Composable
fun PhoneLayout() {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        item { Header() }
        items(sportsList) { sport ->
            SportCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = sport.name,
                imageResId = sport.imageResId,
                imageAlignment = sport.imageAlignment,
                cardColor = PrimaryGreen
            )
        }
    }
}

@Composable
fun TabletLayout() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Header()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sportsList) { sport ->
                SportCard(
                    modifier = Modifier.height(220.dp),
                    text = sport.name,
                    imageResId = sport.imageResId,
                    imageAlignment = sport.imageAlignment,
                    cardColor = PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun Header() {
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

@Composable
fun SportCard(
    modifier: Modifier = Modifier,
    text: String,
    imageResId: Int,
    cardColor: Color,
    imageAlignment: Alignment = Alignment.CenterStart
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .height(190.dp)
            .clickable { },
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
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = PrimaryGreen,
        contentColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { },
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
            onClick = { },
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
            onClick = {
                navController.navigate("profile")
            },
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