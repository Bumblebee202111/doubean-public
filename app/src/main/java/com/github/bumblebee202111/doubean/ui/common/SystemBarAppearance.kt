package com.github.bumblebee202111.doubean.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.github.bumblebee202111.doubean.core.theme.DoubeanTheme

@Composable
fun ScopedDarkSystemBars() {
    val view = LocalView.current
    val defaultLightBars = !DoubeanTheme.isDark
    if (view.isInEditMode) return

    DisposableEffect(view, defaultLightBars) {
        val window = (view.context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.isAppearanceLightStatusBars = false
        controller?.isAppearanceLightNavigationBars = false
        onDispose {
            controller?.isAppearanceLightStatusBars = defaultLightBars
            controller?.isAppearanceLightNavigationBars = defaultLightBars
        }
    }
}