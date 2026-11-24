package com.github.bumblebee202111.doubean.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.github.bumblebee202111.doubean.core.theme.DoubeanTheme
import com.github.bumblebee202111.doubean.util.parseHexColorOrNull

@Composable
fun rememberHeaderWashColor(rawHex: String?): Color {
    val isDark = DoubeanTheme.isDark
    val fallback = MaterialTheme.colorScheme.primary
    return remember(rawHex, isDark, fallback) {
        val base = parseHexColorOrNull(rawHex) ?: fallback
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(base.toArgb(), hsl)
        hsl[1] = hsl[1].coerceAtMost(0.60f)
        hsl[2] = if (isDark) hsl[2].coerceIn(0.35f, 0.60f) else hsl[2].coerceIn(0.42f, 0.72f)
        Color(ColorUtils.HSLToColor(hsl)).copy(alpha = if (isDark) 0.26f else 0.22f)
    }
}

fun Modifier.headerWashBackground(
    washColor: Color,
    surfaceColor: Color,
    collapsedFraction: () -> Float,
): Modifier = drawBehind {
    drawRect(surfaceColor)
    val alpha = washColor.alpha * (1f - collapsedFraction().coerceIn(0f, 1f))
    if (alpha > 0f) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    washColor.copy(alpha = alpha),
                    washColor.copy(alpha = 0f),
                )
            )
        )
    }
}