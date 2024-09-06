package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.groupsHomeScreen
import com.github.bumblebee202111.doubean.feature.profile.navigation.profileScreen
import com.github.bumblebee202111.doubean.feature.statuses.navigation.statusesScreen

@Composable
fun BottomNavHost(
    navController: NavHostController,
    startDestination: Any,
    navigateToSearch: () -> Unit,
    navigateToNotifications: () -> Unit,
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
        statusesScreen(onSettingsClick = navigateToSettings)
        groupsHomeScreen(
            onSearchClick = navigateToSearch,
            onNotificationsClick = navigateToNotifications,
            onSettingsClick = navigateToSettings,
            onGroupClick = navigateToGroupDetail,
            onTopicClick = navigateToTopic
        )
        profileScreen(onLoginClick = navigateToLogin)
    }
}
