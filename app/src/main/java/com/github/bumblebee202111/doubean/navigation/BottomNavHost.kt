package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.groupsHomeScreen
import com.github.bumblebee202111.doubean.feature.home.navigation.homeScreen
import com.github.bumblebee202111.doubean.feature.notifications.navigation.notificationsScreen
import com.github.bumblebee202111.doubean.feature.profile.navigation.profileScreen

@Composable
fun BottomNavHost(
    navController: NavHostController,
    startDestination: Any,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToGroupDetail: (groupId: String, defaultTabId: String?) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeScreen(onSettingsClick = navigateToSettings)
        groupsHomeScreen(
                onSearchClick = navigateToSearch,
                onSettingsClick = navigateToSettings,
                onGroupClick = navigateToGroupDetail,
                onTopicClick = navigateToTopic
            )
        notificationsScreen(
                onTopicClick = navigateToTopic,
                onSettingsClick = navigateToSettings
            )
        profileScreen(onLoginClick = navigateToLogin)
    }
}
