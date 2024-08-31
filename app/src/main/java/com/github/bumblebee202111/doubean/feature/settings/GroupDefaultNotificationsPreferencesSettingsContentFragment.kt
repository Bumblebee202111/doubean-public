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




@AndroidEntryPoint
class GroupDefaultNotificationsPreferencesSettingsContentFragment : PreferenceFragmentCompat() {

    private val viewModel: GroupDefaultNotificationsPreferencesSettingsViewModel by viewModels()

    private var postNotificationsSwitchPreference: SwitchPreferenceCompat? = null
    private var allowDuplicateNotificationsSwitchPreference: SwitchPreferenceCompat? = null
    private var sortRecommendedPostsByListPreference: ListPreference? = null
    private var feedRequestPostCountLimitSeekBarPreference: SeekBarPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.per_follow_default_notifications_preferences, rootKey)
        postNotificationsSwitchPreference =
            preferenceManager.findPreference("post_notifications")
        allowDuplicateNotificationsSwitchPreference =
            preferenceManager.findPreference("allow_duplicate_notifications")
        sortRecommendedPostsByListPreference =
            preferenceManager.findPreference("sort_recommended_topics_by")
        feedRequestPostCountLimitSeekBarPreference =
            preferenceManager.findPreference("feed_request_topic_count_limit")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postNotificationsSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                viewModel.toggleEnablePostNotifications()
                true
            }

        allowDuplicateNotificationsSwitchPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                viewModel.toggleAllowDuplicateNotifications()
                true
            }

        sortRecommendedPostsByListPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                viewModel.setSortRecommendedPostsBy(getSortRecommendedPostsBy(newValue as String))
                true
            }

        feedRequestPostCountLimitSeekBarPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                viewModel.setFeedRequestPostCountLimit(newValue as Int)
                true
            }

        repeatWithViewLifecycle {
            launch {
                viewModel.enablePostNotifications.collect {
                    if (it != null) {
                        postNotificationsSwitchPreference?.isChecked = it
                    }
                }
            }
            launch {
                viewModel.allowDuplicateNotifications.collect {
                    if (it != null) {
                        allowDuplicateNotificationsSwitchPreference?.isChecked = it
                    }
                }
            }
            launch {
                viewModel.sortRecommendedPostsBy.collect {
                    sortRecommendedPostsByListPreference?.value =
                        it?.let(::getSortRecommendedPostsByValue)
                }
            }
            launch {
                viewModel.feedRequestPostCountLimit.collect {
                    if (it != null) {
                        feedRequestPostCountLimitSeekBarPreference?.value = it
                    }
                }
            }
        }
    }

    private fun getSortRecommendedPostsBy(value: String) =
        when (value) {
            getString(R.string.last_updated_value) -> TopicSortBy.LAST_UPDATED
            getString(R.string.new_top_value) -> TopicSortBy.NEW_TOP
            else -> throw IndexOutOfBoundsException()
        }

    private fun getSortRecommendedPostsByValue(topicSortBy: TopicSortBy) =
        when (topicSortBy) {
            TopicSortBy.LAST_UPDATED -> getString(R.string.last_updated_value)
            TopicSortBy.NEW_TOP -> getString(R.string.new_top_value)
            else -> throw IndexOutOfBoundsException()
        }

}