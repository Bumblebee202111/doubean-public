package com.github.bumblebee202111.doubean.util

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