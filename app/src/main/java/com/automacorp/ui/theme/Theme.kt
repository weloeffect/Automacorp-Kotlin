package com.automacorp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Import your color definitions
import com.automacorp.ui.theme.Purple500

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    onPrimary = Color.White,
    // Add other colors as needed
)

@Composable
fun AutomacorpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}