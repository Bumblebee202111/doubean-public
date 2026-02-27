package com.github.bumblebee202111.doubean.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.settings.SettingsScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object SettingsNavKey : NavKey

fun Navigator.navigateToSettings() = navigate(key = SettingsNavKey)

fun EntryProviderScope<NavKey>.settingsEntry(
    onBackClick: () -> Unit,
    onGroupDefaultNotificationsPreferencesSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
) = entry<SettingsNavKey> {
    SettingsScreen(
        onBackClick = onBackClick,
        onGroupDefaultNotificationsPreferencesSettingsClick =
        onGroupDefaultNotificationsPreferencesSettingsClick,
        onLoginClick = onLoginClick
    )
}