package com.github.bumblebee202111.doubean.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun StatusBarIconsEffect(useDarkIcons: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(key1 = useDarkIcons, key2 = view, key3 = context) {
        val window = (context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)
        val originalAppearanceLightStatusBars = insetsController.isAppearanceLightStatusBars

        insetsController.isAppearanceLightStatusBars = useDarkIcons

        onDispose {
            if (insetsController.isAppearanceLightStatusBars == useDarkIcons) {
                insetsController.isAppearanceLightStatusBars = originalAppearanceLightStatusBars
            }
        }
    }
}