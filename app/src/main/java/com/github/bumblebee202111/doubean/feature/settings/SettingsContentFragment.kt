package com.github.bumblebee202111.doubean.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.github.bumblebee202111.doubean.BuildConfig
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// PreferenceFragmentCompat + DataStore
// See https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:datastore/datastore-sampleapp/src/main/java/com/example/datastoresampleapp/SettingsFragment.kt

@AndroidEntryPoint
class SettingsContentFragment : PreferenceFragmentCompat() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private var groupDefaultNotificationsPreferencesPreference: Preference? = null
    private var startAppWithGroupsPreference: SwitchPreferenceCompat? = null
    private var notificationsSwitchPreference: SwitchPreferenceCompat? = null
    private var appVersionPreference: Preference? = null
    lateinit var onGroupDefaultNotificationsPreferencesSettingsClick: () -> Unit

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        groupDefaultNotificationsPreferencesPreference =
            preferenceManager.findPreference("per_follow_default_notifications_preferences")
        startAppWithGroupsPreference =
            preferenceManager.findPreference("start_app_with_groups")
        notificationsSwitchPreference =
            preferenceManager.findPreference("notifications")
        appVersionPreference = preferenceManager.findPreference("app_version")
        appVersionPreference?.summary =
            "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                settingsViewModel.toggleEnableNotifications()
                true
            }

        groupDefaultNotificationsPreferencesPreference?.setOnPreferenceClickListener {
            onGroupDefaultNotificationsPreferencesSettingsClick()
            true
        }

        startAppWithGroupsPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                settingsViewModel.toggleSetGroupsAsStartDestination()
                true
            }

        repeatWithViewLifecycle {
            launch {
                settingsViewModel.enableNotifications.collect {
                    if (it != null) {
                        notificationsSwitchPreference?.isChecked = it
                    }
                }
            }

            launch {
                settingsViewModel.startAppWithGroups.collect {
                    if (it != null) {
                        startAppWithGroupsPreference?.isChecked = it
                    }
                }
            }
        }

    }

}