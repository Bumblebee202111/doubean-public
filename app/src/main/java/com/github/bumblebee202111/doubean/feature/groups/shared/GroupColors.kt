package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.github.bumblebee202111.doubean.core.theme.DoubeanTheme
import com.github.bumblebee202111.doubean.util.parseHexColorOrNull

@Composable
fun rememberGroupTonalSurfaceColor(rawHex: String?): Color {
    val isDark = DoubeanTheme.isDark
    val surface = MaterialTheme.colorScheme.surface
    val neutralContainer = MaterialTheme.colorScheme.surfaceContainerHigh
    return remember(rawHex, isDark, surface, neutralContainer) {
        val parsed = parseHexColorOrNull(rawHex) ?: return@remember surface
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(parsed.toArgb(), hsl)
        if (hsl[1] < 0.08f) return@remember neutralContainer

        hsl[1] = hsl[1].coerceIn(0.24f, 0.42f)
        hsl[2] = if (isDark) 0.18f else 0.88f
        Color(ColorUtils.HSLToColor(hsl))
    }
}

@Composable
fun rememberGroupAccentColor(rawHex: String?): Color {
    val isDark = DoubeanTheme.isDark
    val fallback = MaterialTheme.colorScheme.primary
    return remember(rawHex, isDark, fallback) {
        val parsed = parseHexColorOrNull(rawHex) ?: return@remember fallback
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(parsed.toArgb(), hsl)
        if (hsl[1] < 0.08f) return@remember fallback

        hsl[1] = hsl[1].coerceIn(0.28f, 0.65f)
        hsl[2] = if (isDark) 0.76f else 0.36f
        Color(ColorUtils.HSLToColor(hsl))
    }
}