package com.github.bumblebee202111.doubean.navigation

import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.bumblebee202111.doubean.ui.BottomNavDestination

fun NavController.navigateWithDeepLinkUrl(url: String) {
    val request =
        NavDeepLinkRequest.Builder.fromUri(url.toUri()).build()
    navigate(request)
}

fun NavController.navigateToBottomNavDestination(bottomNavDestination: BottomNavDestination) {
    return navigate(bottomNavDestination.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        
        
        launchSingleTop = true
        
        restoreState = true
    }
}