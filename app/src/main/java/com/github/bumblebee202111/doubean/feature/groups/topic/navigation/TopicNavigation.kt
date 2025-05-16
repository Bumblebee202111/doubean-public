package com.github.bumblebee202111.doubean.feature.groups.topic.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.groups.topic.TopicScreen
import kotlinx.serialization.Serializable

@Serializable
data class TopicRoute(
    val topicId: String,
)

fun NavController.navigateToTopic(topicId: String) = navigate(route = TopicRoute(topicId))

fun NavGraphBuilder.topicScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onUserClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Boolean,
) = composable<TopicRoute>(
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "https://github.com/Bumblebee202111/doubean/group/topic/{topicId}"
        }
    )
) {
    TopicScreen(
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl
    )
}
