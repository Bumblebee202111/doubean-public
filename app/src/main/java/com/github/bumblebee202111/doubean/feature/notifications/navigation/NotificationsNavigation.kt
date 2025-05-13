package com.github.bumblebee202111.doubean.feature.notifications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.notifications.NotificationsScreen
import kotlinx.serialization.Serializable

@Serializable
data object NotificationsRoute

fun NavController.navigateToNotifications() = navigate(route = NotificationsRoute)

fun NavGraphBuilder.notificationsScreen(
    onBackClick: () -> Unit,
    onTopicClick: (topicId: String) -> Unit,
    onGroupClick: (groupId: String) -> Unit,
    onSettingsClick: () -> Unit,
) = composable<NotificationsRoute> {
    NotificationsScreen(
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onGroupClick = onGroupClick,
        onSettingsClick = onSettingsClick
    )
}