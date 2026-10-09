package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSanctuaryTheme = staticCompositionLocalOf { SanctuaryTheme.DARK_ACADEMIA }

@Composable
fun LockInTheme(
    sanctuaryTheme: SanctuaryTheme = SanctuaryTheme.DARK_ACADEMIA,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = sanctuaryTheme.accentColor,
        onPrimary = sanctuaryTheme.accentTextColor,
        primaryContainer = sanctuaryTheme.accentSoftColor,
        onPrimaryContainer = sanctuaryTheme.textColor,
        surface = sanctuaryTheme.cardBg,
        onSurface = sanctuaryTheme.textColor,
        surfaceVariant = sanctuaryTheme.cardBg,
        onSurfaceVariant = sanctuaryTheme.subtextColor,
        background = sanctuaryTheme.backgroundFallback,
        onBackground = sanctuaryTheme.textColor,
        outline = sanctuaryTheme.borderColor
    )

    CompositionLocalProvider(LocalSanctuaryTheme provides sanctuaryTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LockInTypography,
            content = content
        )
    }
}
