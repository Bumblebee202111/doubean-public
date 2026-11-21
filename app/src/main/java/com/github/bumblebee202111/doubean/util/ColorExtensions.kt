package com.github.bumblebee202111.doubean.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun parseHexColorOrNull(raw: String?): Color? {
    val clean = raw?.trim()?.trimStart('#') ?: return null
    val normalized = when (clean.length) {
        6 -> "FF$clean"
        8 -> clean
        else -> return null
    }
    val colorLong = normalized.toLongOrNull(16) ?: return null
    return Color(colorLong)
}

@Deprecated("Use parseHexColorOrNull instead")
fun String.toColor(): Color =
    requireNotNull(parseHexColorOrNull(this)) {
        "Invalid hex color: $this. Use #RRGGBB or #AARRGGBB."
    }

@Deprecated("Use feature or ui/common color helpers instead")
@Composable
fun String?.toColorOrPrimary(): Color =
    parseHexColorOrNull(this) ?: MaterialTheme.colorScheme.primary