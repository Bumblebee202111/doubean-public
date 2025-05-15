package com.github.bumblebee202111.doubean.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun String.toColor(): Color {

    var colorStr = this.replaceFirstChar { if (it == '#') "" else it.toString() }

    require(colorStr.length == 6 || colorStr.length == 8) {
        "Invalid hex color: $this. Use #RRGGBB or #AARRGGBB."
    }

    if (colorStr.length == 6) {
        colorStr = "FF$colorStr"
    }

    return Color(colorStr.toLong(16))
}

@Composable
fun String?.toColorOrPrimary(): Color {
    return this?.let { hexString ->
        try {
            hexString.toColor()
        } catch (e: IllegalArgumentException) {
            MaterialTheme.colorScheme.primary
        }
    } ?: MaterialTheme.colorScheme.primary
}

