package com.github.bumblebee202111.doubean.feature.groups.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.home.GroupsHomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object GroupsHomeNavKey : NavKey

fun EntryProviderScope<NavKey>.groupsHomeEntry(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onGroupClick: (groupId: String, tabId: String?) -> Unit,
    onTopicClick: (uri: String) -> Unit,
) = entry<GroupsHomeNavKey> {
    GroupsHomeScreen(
        onAvatarClick = onAvatarClick,
        onSearchClick = onSearchClick,
        onNotificationsClick = onNotificationsClick,
        onGroupClick = onGroupClick,
        onTopicClick = onTopicClick
    )
}
