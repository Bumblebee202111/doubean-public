package com.github.bumblebee202111.doubean.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.settings.GroupDefaultNotificationsPreferencesSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object
GroupDefaultNotificationsPreferencesSettingsRoute

fun NavController.navigateToGroupDefaultNotificationsPreferencesSettings() =
    navigate(route = GroupDefaultNotificationsPreferencesSettingsRoute)

fun NavGraphBuilder.groupDefaultNotificationsPreferencesSettingsScreen(onBackClick: () -> Unit) =
    composable<GroupDefaultNotificationsPreferencesSettingsRoute> {
        GroupDefaultNotificationsPreferencesSettingsScreen(
            onBackClick = onBackClick,
        )
    }