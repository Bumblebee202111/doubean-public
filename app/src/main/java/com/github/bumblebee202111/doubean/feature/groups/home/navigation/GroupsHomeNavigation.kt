package com.github.bumblebee202111.doubean.feature.groups.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.groups.home.GroupsHomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object GroupsHomeRoute

fun NavGraphBuilder.groupsHomeScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onTopicClick: (topicId: String) -> Unit,
) = composable<GroupsHomeRoute>(
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
        onAvatarClick = onAvatarClick,
        onSearchClick = onSearchClick,
        onNotificationsClick = onNotificationsClick,
        onGroupClick = onGroupClick,
        onTopicClick = onTopicClick
    )
}