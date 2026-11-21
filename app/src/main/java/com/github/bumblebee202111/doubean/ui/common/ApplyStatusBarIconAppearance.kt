package com.github.bumblebee202111.doubean.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.github.bumblebee202111.doubean.core.theme.DoubeanTheme

@Composable
fun ApplyStatusBarIconAppearance(useLightIcons: Boolean?) {
    val view = LocalView.current
    val isDarkTheme = DoubeanTheme.isDark

    if (view.isInEditMode) return

    val targetIsAppearanceLightStatusBars: Boolean = when (useLightIcons) {
        true -> false
        false -> true
        null -> !isDarkTheme
    }
    val targetIsAppearanceLightNavigationBars = !isDarkTheme

    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(window, view)
        if (insetsController.isAppearanceLightStatusBars != targetIsAppearanceLightStatusBars) {
            insetsController.isAppearanceLightStatusBars = targetIsAppearanceLightStatusBars
        }
        if (insetsController.isAppearanceLightNavigationBars != targetIsAppearanceLightNavigationBars) {
            insetsController.isAppearanceLightNavigationBars = targetIsAppearanceLightNavigationBars
        }
    }
}