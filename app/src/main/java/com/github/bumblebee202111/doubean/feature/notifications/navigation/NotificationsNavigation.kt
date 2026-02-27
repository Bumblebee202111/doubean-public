package com.github.bumblebee202111.doubean.feature.notifications.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.notifications.NotificationsScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object NotificationsNavKey : NavKey

fun Navigator.navigateToNotifications() = navigate(key = NotificationsNavKey)

fun EntryProviderScope<NavKey>.notificationsEntry(
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
    onSettingsClick: () -> Unit,
) = entry<NotificationsNavKey> {
    NotificationsScreen(
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onGroupClick = onGroupClick,
        onSettingsClick = onSettingsClick
    )
}