package com.isdenmois.readish.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ReadishColorScheme =
    lightColorScheme(
        primary = Ink,
        onPrimary = Paper,
        primaryContainer = Mist,
        onPrimaryContainer = Ink,
        secondary = MutedInk,
        onSecondary = Paper,
        secondaryContainer = Mist,
        onSecondaryContainer = Ink,
        tertiary = MutedInk,
        onTertiary = Paper,
        tertiaryContainer = Mist,
        onTertiaryContainer = Ink,
        background = Paper,
        onBackground = Ink,
        surface = Paper,
        onSurface = Ink,
        surfaceVariant = Mist,
        onSurfaceVariant = MutedInk,
        surfaceTint = Paper,
        inverseSurface = Ink,
        inverseOnSurface = Paper,
        inversePrimary = Mist,
        outline = Line,
        outlineVariant = Line,
        scrim = Ink,
        error = ErrorInk,
        onError = Paper,
    )

@Composable
fun ReadishTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReadishColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
