package com.isdenmois.readish.shared.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colorPalette = lightColors(
    primary = Color.Black,
    error = Color.Gray,
)

@Composable
fun ReadishTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = colorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
