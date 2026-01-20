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
    onTopicClick: (uri: String) -> Unit,
) = composable<GroupsHomeRoute>(
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "https://m.douban.com/group"
        },
        navDeepLink {
            uriPattern = "https://www.douban.com/group/explore"
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
