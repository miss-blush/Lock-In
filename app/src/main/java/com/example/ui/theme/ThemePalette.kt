package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.R

enum class SanctuaryTheme(
    val displayName: String,
    val cardBg: Color,
    val textColor: Color,
    val subtextColor: Color,
    val accentColor: Color,
    val accentSoftColor: Color,
    val accentTextColor: Color,
    val borderColor: Color,
    val backgroundFallback: Color,
    val drawableRes: Int?
) {
    DARK_ACADEMIA(
        displayName = "Dark Academia",
        cardBg = Color(0xD9120E0C),
        textColor = Color(0xFFF5ECE5),
        subtextColor = Color(0xFFC4B0A2),
        accentColor = Color(0xFFC8955A),
        accentSoftColor = Color(0x40C8955A),
        accentTextColor = Color(0xFFFFFFFF),
        borderColor = Color(0x24F5ECE5),
        backgroundFallback = Color(0xFF0A0908),
        drawableRes = R.drawable.bg_dark_academia
    ),
    RAINY_LIBRARY(
        displayName = "Rainy Library",
        cardBg = Color(0xDB0C1210),
        textColor = Color(0xFFEAF2EF),
        subtextColor = Color(0xFF9FB2AB),
        accentColor = Color(0xFF4A8B82),
        accentSoftColor = Color(0x404A8B82),
        accentTextColor = Color(0xFFFFFFFF),
        borderColor = Color(0x24EAF2EF),
        backgroundFallback = Color(0xFF080F0E),
        drawableRes = R.drawable.bg_rainy_library
    ),
    GOLDEN_SUNFLOWER(
        displayName = "Golden Sunflowers",
        cardBg = Color(0xD9161008),
        textColor = Color(0xFFFFF8EE),
        subtextColor = Color(0xFFD6C2A5),
        accentColor = Color(0xFFD99B26),
        accentSoftColor = Color(0x47D99B26),
        accentTextColor = Color(0xFF171103),
        borderColor = Color(0x26FFF8EE),
        backgroundFallback = Color(0xFF0D0904),
        drawableRes = R.drawable.bg_golden_sunflower
    ),
    ENCHANTED_FOREST(
        displayName = "Enchanted Forest",
        cardBg = Color(0xE00A120E),
        textColor = Color(0xFFE8F5F0),
        subtextColor = Color(0xFF98B8AC),
        accentColor = Color(0xFF3E8E6C),
        accentSoftColor = Color(0x473E8E6C),
        accentTextColor = Color(0xFFFFFFFF),
        borderColor = Color(0x26E8F5F0),
        backgroundFallback = Color(0xFF060D09),
        drawableRes = R.drawable.bg_enchanted_forest
    ),
    TWILIGHT_STREETLAMP(
        displayName = "Twilight Streetlamp",
        cardBg = Color(0xDB0F0E16),
        textColor = Color(0xFFF1EFF7),
        subtextColor = Color(0xFFB4AEC6),
        accentColor = Color(0xFF8A72D6),
        accentSoftColor = Color(0x478A72D6),
        accentTextColor = Color(0xFFFFFFFF),
        borderColor = Color(0x24F1EFF7),
        backgroundFallback = Color(0xFF090810),
        drawableRes = R.drawable.bg_twilight_streetlamp
    )
}
