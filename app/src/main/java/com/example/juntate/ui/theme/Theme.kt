package com.example.juntate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat


//  Modo oscuro (se mantiene definido por si se necesita en el futuro)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = MutedGreen,
    tertiary = AccentPurple,
    background = Color(0xFF1A1A1A),
    surface = SecondaryGray,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White
)

//  Modo claro
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryLightGreen,
    tertiary = AccentPurple,
    background = White,
    surface = LightGray,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = White
)

@Composable
fun JuntateTheme(
    // ✅ CAMBIO 1: Se fuerza el tema claro, ignorando la configuración del sistema.
    darkTheme: Boolean = false,
    // ✅ CAMBIO 2: Se desactiva el color dinámico para usar siempre tu paleta.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Como 'darkTheme' siempre es false, siempre se elegirá LightColorScheme.
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // ✅ Se añade el efecto para controlar la barra de estado.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Se establece el color de la barra de estado y el color de sus íconos (claros u oscuros).
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}