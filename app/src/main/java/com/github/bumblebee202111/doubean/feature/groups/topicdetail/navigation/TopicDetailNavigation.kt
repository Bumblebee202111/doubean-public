package com.github.bumblebee202111.doubean.feature.groups.topicdetail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.groups.topicdetail.TopicDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class TopicDetailRoute(
    val topicId: String,
)

fun NavController.navigateToTopic(topicId: String) = navigate(route = TopicDetailRoute(topicId))

fun NavGraphBuilder.topicDetailScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) = composable<TopicDetailRoute>(
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "https://github.com/Bumblebee202111/doubean/group/topic/{topicId}"
        }
    )
) {
    TopicDetailScreen(
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onShowSnackbar = onShowSnackbar
    )
}
