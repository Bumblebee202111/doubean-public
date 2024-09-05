package com.github.bumblebee202111.doubean.feature.groups.groupDetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailRoute(
    val groupId: String,
    val defaultTabId: String? = null,
)

fun NavController.navigateToGroup(groupId: String, defaultTabId: String? = null) = navigate(
    route = GroupDetailRoute(groupId, defaultTabId)
)

fun NavGraphBuilder.groupDetailScreen(
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) = composable<GroupDetailRoute>(
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "https:
        },
        navDeepLink {
            uriPattern = "https:
        },
    )
) {
    GroupDetailScreen(
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onShowSnackbar = onShowSnackbar
    )
}