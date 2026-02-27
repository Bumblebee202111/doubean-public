package com.github.bumblebee202111.doubean.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.github.bumblebee202111.doubean.MainActivityViewModel
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.login.navigation.navigateToLogin
import com.github.bumblebee202111.doubean.feature.mydoulists.navigation.navigateToMyDouLists
import com.github.bumblebee202111.doubean.feature.settings.navigation.navigateToSettings
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.navigateToUserProfile
import com.github.bumblebee202111.doubean.model.fangorns.User
import com.github.bumblebee202111.doubean.navigation.BottomNavSceneStrategy
import com.github.bumblebee202111.doubean.navigation.NavigationState
import com.github.bumblebee202111.doubean.navigation.Navigator
import com.github.bumblebee202111.doubean.navigation.UserDrawerHeader
import com.github.bumblebee202111.doubean.navigation.createDoubeanEntryProvider
import com.github.bumblebee202111.doubean.navigation.toEntries
import kotlinx.coroutines.launch

@Composable
fun MainNavScreen(
    navigationState: NavigationState,
    navigator: Navigator,
    topLevelRoutes: Set<NavKey>,
    modifier: Modifier = Modifier,
    initialDeepLinkKey: NavKey? = null,
    viewModel: MainActivityViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(initialDeepLinkKey) {
        if (initialDeepLinkKey != null) {
            navigator.navigate(initialDeepLinkKey)
        }
    }

    val bottomNavStrategy = remember(topLevelRoutes, navigator) {
        BottomNavSceneStrategy(topLevelRoutes, navigator)
    }

    val sceneStrategy = remember(bottomNavStrategy) {
        DialogSceneStrategy<NavKey>() then bottomNavStrategy
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerSheet(
                currentUser = currentUser,
                onHeaderClick = {
                    scope.launch { drawerState.close() }
                    if (currentUser != null) {
                        navigator.navigateToUserProfile(currentUser!!.uid)
                    } else {
                        navigator.navigateToLogin()
                    }
                },
                onCollectClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigateToMyDouLists()
                },
                onSettingsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigateToSettings()
                }
            )
        }
    ) {
        NavDisplay(
            entries = navigationState.toEntries(
                createDoubeanEntryProvider(navigator) {
                    scope.launch { drawerState.open() }
                }
            ),
            onBack = { navigator.goBack() },
            sceneStrategy = sceneStrategy,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun NavigationDrawerSheet(
    currentUser: User?,
    onHeaderClick: () -> Unit,
    onCollectClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            UserDrawerHeader(currentUser = currentUser, onHeaderClick = onHeaderClick)
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            NavigationDrawerItem(
                label = { Text(stringResource(id = R.string.title_slide_menu_collect)) },
                selected = false,
                onClick = onCollectClick,
                icon = { Icon(Icons.Default.Collections, contentDescription = null) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.settings)) },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = onSettingsClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}