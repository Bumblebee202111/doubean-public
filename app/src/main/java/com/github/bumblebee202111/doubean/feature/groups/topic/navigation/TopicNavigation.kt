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
    val spmId: String? = null,
)

fun NavController.navigateToTopic(topicId: String, spmId: String? = null) =
    navigate(route = TopicRoute(topicId, spmId))

fun NavGraphBuilder.topicScreen(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onUserClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String, showSnackbarOnFailure: Boolean) -> Boolean,
) = composable<TopicRoute>(
    deepLinks = listOf(
        navDeepLink {
            uriPattern =
                "https://github.com/Bumblebee202111/doubean/group/topic/{topicId}?_spm_id={spmId}"
        },
        navDeepLink {
            uriPattern = "douban://douban.com/group/topic/{topicId}?_spm_id={spmId}"
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
