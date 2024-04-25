package com.github.bumblebee202111.doubean.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.github.bumblebee202111.doubean.BuildConfig
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch




@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    private var perFollowDefaultNotificationsPreferencesPreference: Preference? = null
    private var startAppWithGroupsPreference: SwitchPreferenceCompat? = null
    private var notificationsSwitchPreference: SwitchPreferenceCompat? = null
    private var appVersionPreference: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        perFollowDefaultNotificationsPreferencesPreference =
            preferenceManager.findPreference("per_follow_default_notifications_preferences")
        startAppWithGroupsPreference =
            preferenceManager.findPreference("start_app_with_groups")
        notificationsSwitchPreference =
            preferenceManager.findPreference("notifications")
        appVersionPreference = preferenceManager.findPreference("app_version")
        appVersionPreference?.summary =
            BuildConfig.VERSION_NAME
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val linearLayout =
            super.onCreateView(inflater, container, savedInstanceState) as LinearLayout
        linearLayout.fitsSystemWindows = true
        addToolbar(linearLayout)
        return linearLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        notificationsSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                settingsViewModel.toggleEnableNotifications()
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

    private fun addToolbar(linearLayout: LinearLayout) {
        
        val toolbar = LayoutInflater.from(context)
            .inflate(R.layout.toolbar_settings, linearLayout, false) as MaterialToolbar
        toolbar.setNavigationOnClickListener { v ->
            findNavController(v).navigateUp()
        }
        linearLayout.addView(toolbar, 0)
    }
}