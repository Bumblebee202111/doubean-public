package com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.GroupDetailScreen
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.GroupDetailViewModel
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailNavKey(
    val groupId: String,
    val defaultTabId: String? = null,
) : NavKey

fun Navigator.navigateToGroup(groupId: String, defaultTabId: String? = null) = navigate(
    key = GroupDetailNavKey(groupId, defaultTabId)
)

fun EntryProviderScope<NavKey>.groupDetailEntry(
    onBackClick: () -> Unit,
    onTopicClick: (uri: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
) = entry<GroupDetailNavKey> { key ->
    GroupDetailScreen(
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onUserClick = onUserClick,
        viewModel = hiltViewModel<GroupDetailViewModel, GroupDetailViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(groupId = key.groupId, initialTabId = key.defaultTabId)
            }
        )
    )
}
