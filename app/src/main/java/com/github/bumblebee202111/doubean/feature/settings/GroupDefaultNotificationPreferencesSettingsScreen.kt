package com.github.bumblebee202111.doubean.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.MaxTopicNotificationsPerFetchTextField
import com.github.bumblebee202111.doubean.feature.groups.shared.SortTopicsByOption
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.ClickablePreferenceItem
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.RadioButtonItem
import com.github.bumblebee202111.doubean.ui.component.SelectionDialog
import com.github.bumblebee202111.doubean.ui.component.SwitchPreferenceItem

@Composable
fun GroupDefaultNotificationsPreferencesSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: GroupDefaultNotificationPreferencesSettingsViewModel = hiltViewModel(),

    ) {
    val defaultGroupNotificationPreferences by viewModel.defaultGroupNotificationPreferences.collectAsStateWithLifecycle()
    GroupDefaultNotificationsPreferencesSettingsScreen(
        preferences = defaultGroupNotificationPreferences,
        toggleEnableNotifications = viewModel::toggleEnableNotifications,
        toggleNotifyOnUpdates = viewModel::toggleNotifyOnUpdates,
        setSortBy = viewModel::setSortBy,
        setMaxTopicNotificationsPerFetch = viewModel::setMaxTopicNotificationsPerFetch,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDefaultNotificationsPreferencesSettingsScreen(
    preferences: GroupNotificationPreferences?,
    toggleEnableNotifications: () -> Unit,
    toggleNotifyOnUpdates: () -> Unit,
    setSortBy: (TopicSortBy) -> Unit,
    setMaxTopicNotificationsPerFetch: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    var showSortByDialog by remember { mutableStateOf(false) }
    var showMaxTopicNotificationsPerFetch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleResId = R.string.group_default_notification_preferences_settings_title,
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                })
        }
    ) { innerPadding ->
        if (preferences != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = innerPadding
            ) {

                item {
                    SwitchPreferenceItem(
                        title = stringResource(R.string.notify_on_updates_title),
                        checked = preferences.notifyOnUpdates,
                        onCheckedChange = { toggleNotifyOnUpdates() }
                    )
                }

                item {
                    ClickablePreferenceItem(
                        title = stringResource(R.string.sort_by_title),
                        onClick = { showSortByDialog = true },
                        summary = stringResource(SortTopicsByOption.entries.first { option -> option.sortBy == preferences.sortBy }.textRes)
                    )
                }

                item {
                    ClickablePreferenceItem(
                        title = stringResource(R.string.max_topic_notifications_per_fetch_title),
                        summary = preferences.maxTopicNotificationsPerFetch.toString(),
                        onClick = { showMaxTopicNotificationsPerFetch = true }
                    )

                }
            }
            if (showSortByDialog) {
                SortByDialog(
                    selected = preferences.sortBy,
                    onDismissRequest = { showSortByDialog = false },
                    onSelect = { setSortBy(it) }
                )
            }

            if (showMaxTopicNotificationsPerFetch) {
                MaxTopicNotificationsPerFetchDialog(
                    currentValue = preferences.maxTopicNotificationsPerFetch,
                    onDismiss = { showMaxTopicNotificationsPerFetch = false },
                    onConfirm = setMaxTopicNotificationsPerFetch
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortByDialog(
    selected: TopicSortBy?,
    onDismissRequest: () -> Unit,
    onSelect: (TopicSortBy) -> Unit,
) {
    val options = SortTopicsByOption.entries

    SelectionDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.sort_by_title),
        content = {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                options.forEach { option ->
                    RadioButtonItem(
                        label = stringResource(option.textRes),
                        selected = selected == option.sortBy,
                        onSelect = {
                            onSelect(option.sortBy)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun MaxTopicNotificationsPerFetchDialog(
    currentValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var value by remember {
        mutableIntStateOf(currentValue)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.max_topic_notifications_per_fetch_title)) },
        text = {
            MaxTopicNotificationsPerFetchTextField(
                maxTopicNotificationsPerFetch = value,
                onUpdateMaxTopicNotificationsPerFetch = {
                    value = it
                },
                requestFocus = true,
                fillMaxWidth = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(value)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}



