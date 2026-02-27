package com.github.bumblebee202111.doubean.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.settings.GroupDefaultNotificationsPreferencesSettingsScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object
GroupDefaultNotificationsPreferencesSettingsNavKey : NavKey

fun Navigator.navigateToGroupDefaultNotificationsPreferencesSettings() =
    navigate(key = GroupDefaultNotificationsPreferencesSettingsNavKey)

fun EntryProviderScope<NavKey>.groupDefaultNotificationsPreferencesSettingsEntry(onBackClick: () -> Unit) =
    entry<GroupDefaultNotificationsPreferencesSettingsNavKey> {
        GroupDefaultNotificationsPreferencesSettingsScreen(
            onBackClick = onBackClick
        )
    }