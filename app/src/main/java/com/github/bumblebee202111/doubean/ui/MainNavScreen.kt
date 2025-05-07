package com.github.bumblebee202111.doubean.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.github.bumblebee202111.doubean.navigation.BottomNavRoute
import com.github.bumblebee202111.doubean.navigation.MainNavHost

@Composable
fun MainNavScreen(
    navController: NavHostController,
    startWithGroups: Boolean,
    modifier: Modifier = Modifier,
) {
    MainNavHost(
        navController = navController,
        startDestination = BottomNavRoute,
        startWithGroups = startWithGroups,
        modifier = modifier
    )
}