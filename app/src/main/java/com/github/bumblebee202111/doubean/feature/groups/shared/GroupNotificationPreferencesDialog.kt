package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences

@Composable
fun GroupNotificationPreferencesDialog(
    @StringRes titleTextResId: Int,
    initialPreference: GroupNotificationPreferences,
    onDismissRequest: () -> Unit,
    onConfirmation: (preferencesToSave: GroupNotificationPreferences) -> Unit,
) {

    var preferences by remember {
        mutableStateOf(initialPreference)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirmation(preferences)
            }) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(text = stringResource(id = titleTextResId)) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = {
                        Text(stringResource(R.string.enable_topic_notifications_title))
                    },
                    trailingContent = {
                        Switch(
                            checked = preferences.notificationsEnabled,
                            onCheckedChange = {
                                preferences = preferences.copy(notificationsEnabled = it)
                            }
                        )
                    }
                )
                val disabledTextColor =
                    if (preferences.notificationsEnabled)
                        Color.Unspecified
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f).copy(alpha = 0.38f)
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.notify_on_updates_title),
                            color = disabledTextColor
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = preferences.notifyOnUpdates,
                            onCheckedChange = {
                                preferences = preferences.copy(notifyOnUpdates = it)
                            },
                            enabled = preferences.notificationsEnabled
                        )
                    }
                )
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.sort_by_title),
                            color = disabledTextColor
                        )

                    },
                    trailingContent = {
                        SortTopicsByDropDownMenu(
                            initialSortBy = preferences.sortBy, onSortBySelected = {
                                preferences = preferences.copy(sortBy = it)
                            },
                            enabled = preferences.notificationsEnabled
                        )
                    }
                )
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.max_topic_notifications_per_fetch_title),
                            color = disabledTextColor
                        )
                    },
                    trailingContent = {
                        MaxTopicNotificationsPerFetchTextField(
                            maxTopicNotificationsPerFetch = preferences.maxTopicNotificationsPerFetch,
                            onUpdateMaxTopicNotificationsPerFetch = {
                                preferences = preferences.copy(maxTopicNotificationsPerFetch = it)
                            },
                            enabled = preferences.notificationsEnabled
                        )
                    }
                )
            }

        }
    )
}