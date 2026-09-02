package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

// Warm Brown / Mocha Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = WarmCaramel,
    onPrimary = MochaBackgroundDark,
    primaryContainer = WarmAmberSoft,
    onPrimaryContainer = WarmHoney,
    secondary = CreamTextSecondary,
    onSecondary = MochaBackgroundDark,
    background = MochaBackgroundDark,
    onBackground = CreamTextPrimary,
    surface = MochaSurface,
    onSurface = CreamTextPrimary,
    surfaceVariant = MochaSurfaceElevated,
    onSurfaceVariant = CreamTextSecondary,
    outline = MochaBorder,
    outlineVariant = MochaBorderSubtle
)

// Warm Cream Latte Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = BronzeGold,
    onPrimary = Color.White,
    primaryContainer = LatteSurfaceVariant,
    onPrimaryContainer = MochaBackgroundDark,
    secondary = LatteTextSecondary,
    onSecondary = Color.White,
    background = LatteBackground,
    onBackground = LatteTextPrimary,
    surface = LatteSurface,
    onSurface = LatteTextPrimary,
    surfaceVariant = LatteSurfaceVariant,
    onSurfaceVariant = LatteTextSecondary,
    outline = LatteBorder,
    outlineVariant = LatteBorderSubtle
)

@Composable
fun WeatherAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards-compat alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    WeatherAppTheme(
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        content = content
    )
}
