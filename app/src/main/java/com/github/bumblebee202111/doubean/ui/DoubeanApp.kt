package com.github.bumblebee202111.doubean.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailRoute
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.ReshareStatusesRoute
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicRoute
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.UserProfileRoute
import com.github.bumblebee202111.doubean.navigation.BottomNavRoute
import com.github.bumblebee202111.doubean.ui.common.ApplyStatusBarIconAppearance
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager

@Composable
fun DoubeanApp(
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    startWithGroups: Boolean,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = navController
        .currentBackStackEntryAsState().value?.destination

    var bottomNavTabWantsLightIcons by remember { mutableStateOf<Boolean?>(null) }
    val useLightIcons = remember(currentDestination, bottomNavTabWantsLightIcons) {
        when {
            setOf(
                GroupDetailRoute::class, TopicRoute::class, ReshareStatusesRoute::class,
                UserProfileRoute::class
            ).any { currentDestination?.hasRoute(it) == true } -> true

            currentDestination?.hasRoute(BottomNavRoute::class) == true ->
                bottomNavTabWantsLightIcons

            else -> null
        }
    }

    ApplyStatusBarIconAppearance(useLightIcons = useLightIcons)

    val currentMessage by snackbarManager.currentMessage.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val messageToDisplay = currentMessage?.getString()
    LaunchedEffect(currentMessage, snackbarHostState, lifecycleOwner) {
        if (currentMessage != null && messageToDisplay != null) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val result = snackbarHostState.showSnackbar(
                    message = messageToDisplay
                )
                if (result == SnackbarResult.Dismissed && snackbarManager.currentMessage.value == currentMessage) {
                    snackbarManager.messageShown()
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.systemBarsPadding()
            )
        },
    ) {
        MainNavScreen(
            navController = navController,
            snackbarManager = snackbarManager,
            startWithGroups = startWithGroups,
            onActiveTabAppearanceNeeded = { useLight ->
                bottomNavTabWantsLightIcons = useLight
            },
            modifier = Modifier.padding(it)
        )
    }

}