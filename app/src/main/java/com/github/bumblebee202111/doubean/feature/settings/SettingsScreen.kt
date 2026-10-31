package com.github.bumblebee202111.doubean.feature.settings

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.BuildConfig
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.navigation.TopLevelDestination
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.ClickablePreferenceItem
import com.github.bumblebee202111.doubean.ui.component.DangerButtonPreferenceItem
import com.github.bumblebee202111.doubean.ui.component.DoubeanTextButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.InfoDialog
import com.github.bumblebee202111.doubean.ui.component.PreferenceCategoryHeader
import com.github.bumblebee202111.doubean.ui.component.PreferenceDivider
import com.github.bumblebee202111.doubean.ui.component.SwitchPreferenceItem
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onGroupDefaultNotificationsPreferencesSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val enableNotifications by viewModel.enableNotifications.collectAsStateWithLifecycle()
    val startupTab by viewModel.startupTab.collectAsStateWithLifecycle()
    val visibleTabs by viewModel.visibleTabs.collectAsStateWithLifecycle()
    val autoImportSessionAtStartup by viewModel.autoImportSessionAtStartup.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    SettingsScreen(
        enableNotifications = enableNotifications,
        startupTab = startupTab,
        visibleTabs = visibleTabs,
        autoImportSessionAtStartup = autoImportSessionAtStartup,
        isLoggedIn = isLoggedIn,
        onBackClick = onBackClick,
        setStartupTab = viewModel::setStartupTab,
        setVisibleTabs = viewModel::setVisibleTabs,
        toggleEnableNotifications = viewModel::toggleEnableNotifications,
        toggleAutoImportSessionAtStartup = viewModel::toggleAutoImportSessionAtStartup,
        logout = viewModel::logout,
        onGroupDefaultNotificationsPreferencesSettingsClick = onGroupDefaultNotificationsPreferencesSettingsClick,
        onLoginClick = onLoginClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    enableNotifications: Boolean?,
    startupTab: String?,
    visibleTabs: Set<String>?,
    autoImportSessionAtStartup: Boolean?,
    isLoggedIn: Boolean?,
    onBackClick: () -> Unit,
    setStartupTab: (String) -> Unit,
    setVisibleTabs: (Set<String>) -> Unit,
    toggleEnableNotifications: () -> Unit,
    toggleAutoImportSessionAtStartup: () -> Unit,
    logout: () -> Unit,
    onGroupDefaultNotificationsPreferencesSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    var showNotificationInfoDialog by remember { mutableStateOf(false) }
    var showStartupTabDialog by remember { mutableStateOf(false) }
    var showVisibleTabsDialog by remember { mutableStateOf(false) }

    if (showNotificationInfoDialog) {
        InfoDialog(
            onDismissRequest = { showNotificationInfoDialog = false },
            title = stringResource(R.string.notifications_info_title),
            text = stringResource(R.string.notifications_info_body)
        )
    }

    
    if (showStartupTabDialog && startupTab != null && visibleTabs != null) {
        val availableTabs = TopLevelDestination.entries.filter { it.name in visibleTabs }
        AlertDialog(
            onDismissRequest = { showStartupTabDialog = false },
            title = { Text(stringResource(R.string.pref_title_startup_tab)) },
            text = {
                Column {
                    availableTabs.forEach { tab ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    setStartupTab(tab.name)
                                    showStartupTabDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(selected = tab.name == startupTab, onClick = null)
                            Text(
                                stringResource(tab.labelResId),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                DoubeanTextButton(onClick = {
                    showStartupTabDialog = false
                }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }

    if (showVisibleTabsDialog && visibleTabs != null) {
        var selectedTabs by remember { mutableStateOf(visibleTabs) }
        AlertDialog(
            onDismissRequest = { showVisibleTabsDialog = false },
            title = { Text(stringResource(R.string.pref_title_visible_tabs)) },
            text = {
                Column {
                    TopLevelDestination.entries.forEach { tab ->
                        val isMeTab = tab == TopLevelDestination.Me
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isMeTab) {
                                    selectedTabs = if (tab.name in selectedTabs) {
                                        selectedTabs - tab.name
                                    } else {
                                        selectedTabs + tab.name
                                    }
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            Checkbox(
                                checked = tab.name in selectedTabs,
                                onCheckedChange = null,
                                enabled = !isMeTab
                            )
                            Text(
                                stringResource(tab.labelResId),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                DoubeanTextButton(onClick = {
                    setVisibleTabs(selectedTabs)
                    showVisibleTabsDialog = false
                }) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                DoubeanTextButton(onClick = {
                    showVisibleTabsDialog = false
                }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleResId = R.string.settings,
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                })
        }
    ) { innerPadding ->
        val context = LocalContext.current
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = innerPadding) {

            
            item {
                PreferenceCategoryHeader(title = stringResource(R.string.pref_title_navigation))
            }
            if (startupTab != null && visibleTabs != null) {
                item {
                    val currentTabLabel =
                        TopLevelDestination.entries.find { it.name == startupTab }?.labelResId
                    ClickablePreferenceItem(
                        title = stringResource(R.string.pref_title_startup_tab),
                        summary = currentTabLabel?.let { stringResource(it) } ?: "",
                        onClick = { showStartupTabDialog = true }
                    )
                }
                item {
                    ClickablePreferenceItem(
                        title = stringResource(R.string.pref_title_visible_tabs),
                        summary = stringResource(
                            R.string.pref_summary_visible_tabs_count,
                            visibleTabs.size
                        ),
                        onClick = { showVisibleTabsDialog = true }
                    )
                }
            }

            item {
                PreferenceDivider()
            }

            
            item {
                PreferenceCategoryHeader(title = stringResource(R.string.pref_cat_title_notifications))
            }
            if (enableNotifications != null) {
                item {
                    val showWarning = !checkNotificationPermission(context)

                    SwitchPreferenceItem(
                        title = stringResource(R.string.enable_notifications_title),
                        checked = enableNotifications,
                        onCheckedChange = { toggleEnableNotifications() },
                        summary = if (showWarning) {
                            stringResource(
                                id = R.string.notification_permission_tip,
                                stringResource(R.string.app_name)
                            )
                        } else {
                            stringResource(R.string.notification_permission_granted)
                        },
                        onInfoClick = { showNotificationInfoDialog = true }
                    )
                }
            }
            item {
                ClickablePreferenceItem(
                    title = stringResource(R.string.group_default_notification_preferences_settings_title),
                    onClick = onGroupDefaultNotificationsPreferencesSettingsClick
                )
            }

            item {
                PreferenceDivider()
            }

            
            item {
                PreferenceCategoryHeader(stringResource(R.string.title_account_and_session))
            }

            if (autoImportSessionAtStartup != null) {
                item {
                    SwitchPreferenceItem(
                        title = stringResource(R.string.settings_auto_import_title),
                        summary = stringResource(R.string.settings_auto_import_summary),
                        checked = autoImportSessionAtStartup,
                        onCheckedChange = { toggleAutoImportSessionAtStartup() }
                    )
                }
            }

            if (isLoggedIn == false) {
                item {
                    ClickablePreferenceItem(
                        title = stringResource(R.string.settings_open_login_screen),
                        onClick = onLoginClick
                    )
                }
            }

            item {
                PreferenceDivider()
            }

            
            item {
                PreferenceCategoryHeader(title = stringResource(R.string.about_header))
            }
            item {
                ClickablePreferenceItem(
                    title = stringResource(R.string.app_version_title),
                    summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    onClick = {
                        OpenInUtils.viewInActivity(
                            context,
                            "https:
                        )
                    }
                )
            }
            item {
                ClickablePreferenceItem(
                    title = stringResource(R.string.app_author_title),
                    summary = stringResource(R.string.app_author),
                    onClick = {
                        OpenInUtils.viewInActivity(
                            context,
                            "https:
                        )
                    }
                )
            }
            item {
                ClickablePreferenceItem(
                    title = stringResource(R.string.send_feedback_title),
                    onClick = {
                        OpenInUtils.viewInActivity(
                            context,
                            "https:
                        )
                    }
                )
            }

            if (isLoggedIn == true) {
                item {
                    PreferenceDivider()
                }
                item {
                    DangerButtonPreferenceItem(
                        title = stringResource(R.string.settings_logout_button),
                        onClick = logout
                    )
                }

            }

        }
    }

}

private fun checkNotificationPermission(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}