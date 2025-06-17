package com.github.bumblebee202111.doubean.navigation

import android.util.Log
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.app.BottomNavDestination
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage

private const val TAG = "NavControllerExt"

fun NavController.tryNavigateToUri(
    uriString: String,
    snackbarManager: SnackbarManager,
    showSnackbarOnFailure: Boolean = true,
): Boolean {
    try {
        val request = NavDeepLinkRequest.Builder
            .fromUri(uriString.toUri())
            .build()
        this.navigate(request)
        return true
    } catch (e: Exception) {
        Log.e(TAG, "Error navigating to URI: $uriString", e)
        if (showSnackbarOnFailure) {
            snackbarManager.showMessage(
                R.string.error_navigation_failed_with_uri.toUiMessage(uriString)
            )
        }
        return false
    }
}

fun NavController.navigateToBottomNavDestination(bottomNavDestination: BottomNavDestination) {
    return navigate(bottomNavDestination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}