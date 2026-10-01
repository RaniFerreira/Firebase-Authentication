package com.example.autenticacao.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo30,
    onPrimaryContainer = Indigo90,
    secondary = Cyan80,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5C),
    onSecondaryContainer = Cyan90,
    background = Slate10,
    onBackground = Slate90,
    surface = Slate20,
    onSurface = Slate90,
    surfaceVariant = Slate30,
    onSurfaceVariant = Color(0xFFB7BCD0),
    error = Red80,
    onError = Color(0xFF601410)
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo10,
    secondary = Cyan40,
    onSecondary = Color.White,
    secondaryContainer = Cyan90,
    onSecondaryContainer = Color(0xFF001F26),
    background = Slate95,
    onBackground = Slate10,
    surface = Slate99,
    onSurface = Slate10,
    surfaceVariant = Slate90,
    onSurfaceVariant = Color(0xFF4A5068),
    error = Red40,
    onError = Color.White
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun AutenticacaoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Cores dinâmicas desativadas de propósito, para manter a identidade visual do app.
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
