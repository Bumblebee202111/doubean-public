package com.github.bumblebee202111.doubean.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings() = navigate(route = SettingsRoute)

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onGroupDefaultNotificationsPreferencesSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
) = composable<SettingsRoute> {
    SettingsScreen(
        onBackClick = onBackClick,
        onGroupDefaultNotificationsPreferencesSettingsClick =
        onGroupDefaultNotificationsPreferencesSettingsClick,
        onLoginClick = onLoginClick
    )
}