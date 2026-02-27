package com.github.bumblebee202111.doubean.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.GroupDetailNavKey
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.GroupsHomeNavKey
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.ReshareStatusesNavKey
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.TopicNavKey
import com.github.bumblebee202111.doubean.feature.subjects.navigation.SubjectsNavKey
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.UserProfileNavKey
import com.github.bumblebee202111.doubean.navigation.Navigator
import com.github.bumblebee202111.doubean.navigation.TopLevelDestination
import com.github.bumblebee202111.doubean.navigation.rememberNavigationState
import com.github.bumblebee202111.doubean.ui.common.ApplyStatusBarIconAppearance
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager

@Composable
fun DoubeanApp(
    snackbarManager: SnackbarManager,
    startWithGroups: Boolean,
    initialDeepLinkKey: NavKey? = null,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val topLevelRoutes = remember { TopLevelDestination.entries.map { it.route as NavKey }.toSet() }
    val startRoute = if (startWithGroups) GroupsHomeNavKey else SubjectsNavKey

    val navigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = topLevelRoutes
    )
    val navigator = remember { Navigator(navigationState) }

    val currentKey by remember {
        derivedStateOf {
            val currentStack = navigationState.backStacks[navigationState.topLevelRoute]
            currentStack?.lastOrNull() ?: navigationState.topLevelRoute
        }
    }

    val useLightIcons = remember(currentKey) {
        val needsLightIcons = currentKey is GroupDetailNavKey ||
                currentKey is TopicNavKey ||
                currentKey is ReshareStatusesNavKey ||
                currentKey is UserProfileNavKey

        if (needsLightIcons) true else null
    }

    ApplyStatusBarIconAppearance(useLightIcons = useLightIcons)

    val currentMessage by snackbarManager.currentMessage.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val messageToDisplay = currentMessage?.getString()
    LaunchedEffect(currentMessage, snackbarHostState, lifecycleOwner) {
        if (currentMessage != null && messageToDisplay != null) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val result = snackbarHostState.showSnackbar(message = messageToDisplay)
                if (result == SnackbarResult.Dismissed && snackbarManager.currentMessage.value == currentMessage) {
                    snackbarManager.messageShown()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MainNavScreen(
            navigationState = navigationState,
            navigator = navigator,
            topLevelRoutes = topLevelRoutes,
            initialDeepLinkKey = initialDeepLinkKey,
            modifier = Modifier.fillMaxSize()
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding()
        )
    }
}