package com.github.bumblebee202111.doubean.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.github.bumblebee202111.doubean.feature.app.navigation.BottomNavRoute
import com.github.bumblebee202111.doubean.navigation.MainNavHost
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager

@Composable
fun MainNavScreen(
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    startWithGroups: Boolean,
    modifier: Modifier = Modifier,
    onActiveTabAppearanceNeeded: (useLightIcons: Boolean?) -> Unit,
) {
    MainNavHost(
        navController = navController,
        snackbarManager = snackbarManager,
        startDestination = BottomNavRoute,
        startWithGroups = startWithGroups,
        onActiveTabAppearanceNeeded = onActiveTabAppearanceNeeded,
        modifier = modifier
    )
}