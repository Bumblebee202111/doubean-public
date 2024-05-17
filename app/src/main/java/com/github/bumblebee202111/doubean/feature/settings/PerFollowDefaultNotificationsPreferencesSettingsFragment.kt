package com.github.bumblebee202111.doubean.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch




@AndroidEntryPoint
class PerFollowDefaultNotificationsPreferencesSettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: PerFollowDefaultNotificationsPreferencesSettingsViewModel by viewModels()

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
            preferenceManager.findPreference("sort_recommended_posts_by")
        feedRequestPostCountLimitSeekBarPreference =
            preferenceManager.findPreference("feed_request_post_count_limit")
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

    private fun addToolbar(linearLayout: LinearLayout) {
        
        val toolbar = LayoutInflater.from(context)
            .inflate(
                R.layout.toolbar_per_follow_default_notifications_settings,
                linearLayout,
                false
            ) as MaterialToolbar
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        linearLayout.addView(toolbar, 0)
    }
}