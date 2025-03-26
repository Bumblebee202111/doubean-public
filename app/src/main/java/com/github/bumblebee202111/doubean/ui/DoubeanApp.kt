package com.github.bumblebee202111.doubean.ui

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailRoute
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.ReshareStatusesRoute
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicRoute
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.util.uiMessage
import kotlin.reflect.KFunction0

@Composable
fun DoubeanApp(
    navController: NavHostController,
    startWithGroups: Boolean,
    uiError: AppError?,
    clearUiError: KFunction0<Unit>,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentDestination = navController
        .currentBackStackEntryAsState().value?.destination
    val view = LocalView.current
    val context = LocalContext.current

    val forceLightIcons = setOf(
        GroupDetailRoute::class, TopicRoute::class, ReshareStatusesRoute::class
    ).any {
        currentDestination?.hasRoute(it) == true
    }

    DisposableEffect(view, forceLightIcons) {
        val window = (context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)
        val originalIconState = insetsController.isAppearanceLightStatusBars
        if (forceLightIcons) {
            insetsController.isAppearanceLightStatusBars = false
        }
        onDispose {
            insetsController.isAppearanceLightStatusBars = originalIconState
        }
    }

    if (uiError != null) {
        val message = uiError.uiMessage
        LaunchedEffect(uiError) {
            snackbarHostState.showSnackbar(message)
            clearUiError()
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
            startWithGroups = startWithGroups,
            onShowSnackbar = snackbarHostState::showSnackbar,
            modifier = Modifier.padding(it)
        )
    }

}