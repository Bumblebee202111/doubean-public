package com.github.bumblebee202111.doubean.feature.notifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.notifications.NotificationsScreen
import kotlinx.serialization.Serializable

@Serializable
data object NotificationsRoute

fun NavGraphBuilder.notificationsScreen(
    onTopicClick: (topicId: String) -> Unit,
    onSettingsClick: () -> Unit,
) = composable<NotificationsRoute> {
    NotificationsScreen(
        onTopicClick = onTopicClick,
        onSettingsClick = onSettingsClick
    )
}