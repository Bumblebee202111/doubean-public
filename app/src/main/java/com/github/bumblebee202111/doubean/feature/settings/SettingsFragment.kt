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
import com.github.bumblebee202111.doubean.util.InjectorUtils
import com.google.android.material.appbar.MaterialToolbar

// PreferenceFragmentCompat + DataStore
// See https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:datastore/datastore-sampleapp/src/main/java/com/example/datastoresampleapp/SettingsFragment.kt
class SettingsFragment : PreferenceFragmentCompat() {

    private val settingsViewModel: SettingsViewModel by viewModels {
        InjectorUtils.provideSettingsViewModelFactory(
            requireContext()
        )
    }

    private var perFollowDefaultNotificationsPreferencesPreference: Preference? = null
    private var notificationsSwitchPreference: SwitchPreferenceCompat? = null
    private var appVersionPreference: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        perFollowDefaultNotificationsPreferencesPreference =
            preferenceManager.findPreference<Preference>("per_follow_default_notifications_preferences")
        notificationsSwitchPreference =
            preferenceManager.findPreference<SwitchPreferenceCompat>("notifications")
        appVersionPreference = preferenceManager.findPreference<Preference>("app_version")
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
        settingsViewModel.enableNotifications.observe(viewLifecycleOwner) {
            notificationsSwitchPreference?.isChecked = it
        }

        notificationsSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                settingsViewModel.toggleEnableNotifications()
                true
            }
    }

    private fun addToolbar(linearLayout: LinearLayout) {
        // https://blog.csdn.net/m0_46958584/article/details/105663403
        val toolbar = LayoutInflater.from(context)
            .inflate(R.layout.toolbar_settings, linearLayout, false) as MaterialToolbar
        toolbar.setNavigationOnClickListener { v ->
            findNavController(v!!).navigateUp()
        }
        linearLayout.addView(toolbar, 0)
    }
}