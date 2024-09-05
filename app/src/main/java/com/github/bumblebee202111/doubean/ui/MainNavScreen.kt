package com.github.bumblebee202111.doubean.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.bumblebee202111.doubean.navigation.BottomNavRoute
import com.github.bumblebee202111.doubean.navigation.MainNavHost

@Composable
fun MainNavScreen(
    startWithGroups: Boolean,
    onShowSnackbar: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    MainNavHost(
        navController = navController,
        onShowSnackbar = onShowSnackbar,
        startDestination = BottomNavRoute,
        startWithGroups = startWithGroups,
        modifier = modifier
    )
}