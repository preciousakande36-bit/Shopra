package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueLight,
    onPrimary = Color.White,
    primaryContainer = NavyDark,
    onPrimaryContainer = Color.White,
    secondary = AccentGold,
    onSecondary = NavyDark,
    secondaryContainer = NavyLight,
    onSecondaryContainer = AccentGoldLight,
    tertiary = SuccessGreen,
    background = NavyDark,
    surface = NavySurface,
    onBackground = Slate100,
    onSurface = Slate100,
    surfaceVariant = NavyLight,
    onSurfaceVariant = Slate300,
    outline = Slate700
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Slate100,
    onPrimaryContainer = NavyDark,
    secondary = AccentGoldDark,
    onSecondary = Color.White,
    secondaryContainer = AccentGoldLight,
    onSecondaryContainer = NavyDark,
    tertiary = SuccessGreen,
    background = Slate50,
    surface = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200
)

@Composable
fun ShoporaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent brand styling across versions
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
