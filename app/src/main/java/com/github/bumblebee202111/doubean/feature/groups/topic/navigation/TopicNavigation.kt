package com.github.bumblebee202111.doubean.feature.groups.topic.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.topic.TopicScreen
import com.github.bumblebee202111.doubean.feature.groups.topic.TopicViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicNavKey(
    val topicId: String,
    @SerialName("_spm_id")
    val spmId: String? = null,
) : NavKey

fun Navigator.navigateToTopic(topicId: String, spmId: String? = null) =
    navigate(key = TopicNavKey(topicId, spmId))

fun EntryProviderScope<NavKey>.topicEntry(
    onBackClick: () -> Unit,
    onWebViewClick: (url: String) -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onReshareStatusesClick: (topicId: String) -> Unit,
    onUserClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Boolean,
) = entry<TopicNavKey> {
    TopicScreen(
        onBackClick = onBackClick,
        onWebViewClick = onWebViewClick,
        onGroupClick = onGroupClick,
        onReshareStatusesClick = onReshareStatusesClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        viewModel = hiltViewModel<TopicViewModel, TopicViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(topicId = it.topicId, spmId = it.spmId)
            }
        )
    )
}
