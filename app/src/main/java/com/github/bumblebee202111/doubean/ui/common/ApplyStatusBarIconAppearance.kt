package com.github.bumblebee202111.doubean.ui.common

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ApplyStatusBarIconAppearance(useLightIcons: Boolean?) {
    val view = LocalView.current
    val systemIsDark = isSystemInDarkTheme()

    if (view.isInEditMode) return

    val targetIsAppearanceLightBars: Boolean = when (useLightIcons) {
        true -> false
        false -> true
        null -> !systemIsDark
    }

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(window, view)
        if (insetsController.isAppearanceLightStatusBars != targetIsAppearanceLightBars) {
            insetsController.isAppearanceLightStatusBars = targetIsAppearanceLightBars
        }
    }
}