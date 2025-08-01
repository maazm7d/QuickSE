package com.maazm7d.quickse.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color(0xFF121212),

    secondary = Color(0xFFCCCCCC),
    onSecondary = Color(0xFF121212),

    tertiary = Color(0xFFAAAAAA),
    onTertiary = Color.Black,

    background = Color(0xFF000000),
    onBackground = Color.White,

    surface = Color(0xFF000000),
    onSurface = Color.White,

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color.White,

    primaryContainer = Color(0xFF2A2A2A),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color(0xFF1A1A1A),
    onSecondaryContainer = Color.White,

    tertiaryContainer = Color(0xFF444444),
    onTertiaryContainer = Color.Black,

    error = Color(0xFFB00020),
    onError = Color.Black
)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF333333),
    onPrimary = Color.White,

    secondary = Color(0xFF666666),
    onSecondary = Color.White,

    tertiary = Color(0xFF999999),
    onTertiary = Color.Black,

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF111111),

    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF111111),

    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF333333),

    primaryContainer = Color(0xFF444444),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color(0xFF777777),
    onSecondaryContainer = Color.White,

    tertiaryContainer = Color(0xFFBBBBBB),
    onTertiaryContainer = Color.Black,

    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun QuickSETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    val bgColor = colorScheme.background.toArgb()
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = bgColor
            @Suppress("DEPRECATION")
            window.navigationBarColor = bgColor

            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}