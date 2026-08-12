package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = LightSurface,
    primaryContainer = RedDim,
    onPrimaryContainer = DarkText,
    secondary = StatusBlue,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCard2,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkMuted,
    outline = DarkLine
)

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = LightSurface,
    primaryContainer = RedDark,
    onPrimaryContainer = LightSurface,
    secondary = StatusBlue,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightCard2,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = LightMuted,
    outline = LightLine
)

enum class AppThemeMode {
    DARK, LIGHT, SYSTEM
}

@Composable
fun VolteTechnicianTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
