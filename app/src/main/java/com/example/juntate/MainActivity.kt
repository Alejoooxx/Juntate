package com.example.juntate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.juntate.ui.theme.JuntateTheme
import com.example.juntate.ui.theme.screens.*
import com.google.firebase.FirebaseApp
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        setContent {
            JuntateTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "onboarding",
        modifier = modifier
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onStartClick = { navController.navigate("login") }
            )
        }

        composable(
            route = "login?message={message}",
            arguments = listOf(navArgument("message") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")
            LaunchedEffect(message) {
                message?.let {
                    snackbarHostState.showSnackbar(it)
                }
            }
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { successMessage ->
                    navController.navigate("login?message=$successMessage") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("futbol_screen") {
            FutbolScreen(navController = navController)
        }
        composable("fut_event_screen") {
            FutEventScreen(navController = navController)
        }

        composable("history") {
            EventHistoryScreen(navController = navController)
        }

        composable(
            route = "event_details/{sportType}/{eventId}",
            arguments = listOf(
                navArgument("sportType") { type = NavType.StringType },
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sportType = backStackEntry.arguments?.getString("sportType")
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (sportType != null && eventId != null) {
                EventDetailsScreen(
                    navController = navController,
                    sportType = sportType,
                    eventId = eventId
                )
            } else {
                Text("Error: Faltan datos para cargar el evento.")
            }
        }

        composable("running_screen") {
            RunningScreen(navController = navController)
        }
        composable("run_event_screen") {
            RunEventScreen(navController = navController)
        }

        composable("gym_screen") {
            GymScreen(navController = navController)
        }
        composable("gym_event_screen") {
            GymEventScreen(navController = navController)
        }

        composable(
            route = "report_player/{userId}/{userName}/{userPhotoUrl}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType },
                navArgument("userPhotoUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val userName = backStackEntry.arguments?.getString("userName")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.name())
            }
            val userPhotoUrl = backStackEntry.arguments?.getString("userPhotoUrl")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.name())
            }

            if (userId != null && userName != null && userPhotoUrl != null) {
                ReportPlayerScreen(
                    navController = navController,
                    reportedUserId = userId,
                    reportedUserName = userName,
                    reportedUserPhotoUrl = userPhotoUrl
                )
            } else {
                Text("Error: Faltan datos para reportar al usuario.")
            }
        }

        composable("confirm_report") {
            ConfirmReportScreen(navController = navController)
        }
    }
}