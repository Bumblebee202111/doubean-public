package com.github.bumblebee202111.doubean.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// PreferenceFragmentCompat + DataStore<Preferences>
// See https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:datastore/datastore-sampleapp/src/main/java/com/example/datastoresampleapp/SettingsFragment.kt
// Also conforms to MVVM
@AndroidEntryPoint
class GroupDefaultNotificationPreferencesSettingsContentFragment : PreferenceFragmentCompat() {

    private val viewModel: GroupDefaultNotificationPreferencesSettingsViewModel by viewModels()

    private var enableNotificationsSwitchPreference: SwitchPreferenceCompat? = null
    private var notifyOnUpdatesSwitchPreference: SwitchPreferenceCompat? = null
    private var sortByListPreference: ListPreference? = null
    private var maxTopicsPerFetchSeekBarPreference: SeekBarPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.group_default_notifications_preferences, rootKey)
        enableNotificationsSwitchPreference =
            preferenceManager.findPreference("enable_notifications")
        notifyOnUpdatesSwitchPreference =
            preferenceManager.findPreference("notify_on_updates")
        sortByListPreference =
            preferenceManager.findPreference("sort_by")
        maxTopicsPerFetchSeekBarPreference =
            preferenceManager.findPreference("max_topics_per_fetch")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableNotificationsSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                viewModel.toggleEnableNotifications()
                true
            }

        notifyOnUpdatesSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                viewModel.toggleNotifyOnUpdates()
                true
            }

        sortByListPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                viewModel.setSortBy(getSortTopicsBy(newValue as String))
                true
            }

        maxTopicsPerFetchSeekBarPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                viewModel.setMaxTopicsPerFetch(newValue as Int)
                true
            }

        repeatWithViewLifecycle {
            launch {
                viewModel.defaultGroupNotificationPreferences.collect {
                    if (it != null) {
                        enableNotificationsSwitchPreference?.isChecked = it.notificationsEnabled
                        notifyOnUpdatesSwitchPreference?.isChecked = it.notifyOnUpdates
                        sortByListPreference?.value =
                            it.sortBy.let(::getSortTopicsByValue)
                        maxTopicsPerFetchSeekBarPreference?.value = it.maxTopicsPerFetch
                    }
                }
            }
        }
    }

    private fun getSortTopicsBy(value: String) =
        when (value) {
            getString(R.string.new_last_created_value) -> TopicSortBy.NEW_LAST_CREATED
            getString(R.string.new_value) -> TopicSortBy.NEW
            getString(R.string.hot_last_created_value) -> TopicSortBy.HOT_LAST_CREATED
            getString(R.string.hot_value) -> TopicSortBy.HOT
            else -> throw IndexOutOfBoundsException()
        }

    private fun getSortTopicsByValue(topicSortBy: TopicSortBy) =
        when (topicSortBy) {
            TopicSortBy.NEW_LAST_CREATED -> getString(R.string.new_last_created_value)
            TopicSortBy.NEW -> getString(R.string.new_value)
            TopicSortBy.HOT_LAST_CREATED -> getString(R.string.hot_last_created_value)
            TopicSortBy.HOT -> getString(R.string.hot_value)
        }

}