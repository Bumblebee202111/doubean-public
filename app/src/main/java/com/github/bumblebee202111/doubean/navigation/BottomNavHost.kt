package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.groups.home.GroupsHomeRoute
import com.github.bumblebee202111.doubean.feature.groups.home.GroupsHomeScreen
import com.github.bumblebee202111.doubean.feature.home.HomeRoute
import com.github.bumblebee202111.doubean.feature.home.HomeScreen
import com.github.bumblebee202111.doubean.feature.notifications.NotificationsRoute
import com.github.bumblebee202111.doubean.feature.notifications.NotificationsScreen
import com.github.bumblebee202111.doubean.feature.profile.ProfileRoute
import com.github.bumblebee202111.doubean.feature.profile.ProfileScreen

@Composable
fun BottomNavHost(
    navController: NavHostController,
    startDestination: Any,
    navigateToSearch: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToGroupDetail: (groupId: String, tabId: String?) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(navigateToSettings)
        }
        composable<GroupsHomeRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https:
                },
                navDeepLink {
                    uriPattern = "https:
                }
            )
        ) {
            GroupsHomeScreen(
                onSearchClick = navigateToSearch,
                onSettingsClick = navigateToSettings,
                onGroupClick = navigateToGroupDetail,
                onTopicClick = navigateToTopic
            )
        }
        composable<NotificationsRoute> {
            NotificationsScreen(
                onTopicClick = navigateToTopic,
                onSettingsClick = navigateToSettings
            )
        }
        composable<ProfileRoute> {
            ProfileScreen(navigateToLogin)
        }
    }
}
