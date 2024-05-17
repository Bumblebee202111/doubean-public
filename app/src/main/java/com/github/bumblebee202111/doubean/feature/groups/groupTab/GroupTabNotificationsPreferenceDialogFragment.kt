package com.github.bumblebee202111.doubean.feature.groups.groupTab

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.viewModels
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.DialogGroupTabNotificationsPreferenceBinding
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailViewModel
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.github.bumblebee202111.doubean.util.MinMaxEditTextInputFilter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class GroupTabNotificationsPreferenceDialogFragment : AppCompatDialogFragment() {

    private val groupDetailViewModel: GroupDetailViewModel by viewModels({ requireParentFragment() })
    private val groupTabViewModel: GroupTabViewModel by viewModels({ requireParentFragment() })
    lateinit var binding: DialogGroupTabNotificationsPreferenceBinding
    private lateinit var enableGroupNotificationsPref: SwitchMaterial
    private lateinit var allowDuplicateNotificationsPref: SwitchMaterial
    private lateinit var sortRecommendedPostsByTitle: MaterialTextView
    private lateinit var sortRecommendedPostsBySpinner: AppCompatSpinner
    private lateinit var feedRequestPostCountLimitTitle: MaterialTextView
    private lateinit var feedRequestPostCountLimitEditText: TextInputEditText


    var tabId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabId = arguments?.getString(ARG_TAB_ID)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding =
            DialogGroupTabNotificationsPreferenceBinding.inflate(LayoutInflater.from(context))

        enableGroupNotificationsPref = binding.enablePostNotificationsPref
        allowDuplicateNotificationsPref = binding.allowDuplicateNotificationsPref
        sortRecommendedPostsByTitle = binding.sortRecommendedPostsByTitle
        sortRecommendedPostsBySpinner = binding.sortRecommendedPostsBySpinner
        feedRequestPostCountLimitTitle = binding.feedRequestPostCountLimitTitle
        feedRequestPostCountLimitEditText = binding.feedRequestPostCountLimitEditText

        onEnableGroupNotificationsCheckedInitializeOrChange(enableGroupNotificationsPref.isChecked)

        enableGroupNotificationsPref.setOnCheckedChangeListener { _, isChecked ->
            onEnableGroupNotificationsCheckedInitializeOrChange(isChecked)
        }

        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_recommended_posts_by_array,
            android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item) }

        sortRecommendedPostsBySpinner.adapter = arrayAdapter

        feedRequestPostCountLimitEditText.filters =
            arrayOf(MinMaxEditTextInputFilter(1, 50))

        requireParentFragment().repeatWithViewLifecycle {
            launch {
                groupDetailViewModel.group.collect { group ->
                    group?.findTab(tabId)?.let { tab ->
                        tab.enableNotifications?.let { enableGroupNotificationsPref.isChecked = it }
                        tab.allowDuplicateNotifications?.let {
                            allowDuplicateNotificationsPref.isChecked = it
                        }
                        tab.sortRecommendedPostsBy?.let {
                            sortRecommendedPostsBySpinner.setSelection(getSpinnerItemPositionOf(it))
                        }
                        tab.feedRequestPostCountLimit?.let {
                            feedRequestPostCountLimitEditText.setText(
                                it.toString()
                            )
                        }
                    }

                }
            }
        }

        return MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
            .setTitle(getString(R.string.tab_notifications_preference))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                groupTabViewModel.saveNotificationsPreference(
                    enableNotifications = enableGroupNotificationsPref.isChecked,
                    allowNotificationUpdates = allowDuplicateNotificationsPref.isChecked,
                    sortRecommendedPostsBy = getPostSortByAt(
                        sortRecommendedPostsBySpinner.selectedItemPosition
                    ),
                    numberOfPostsLimitEachFeedFetch = feedRequestPostCountLimitEditText.text.toString()
                        .toInt()
                )
            }
            .setNegativeButton(getString(R.string.cancel), null).create()
    }


    private fun getPostSortByAt(spinnerItemPosition: Int) =
        when (spinnerItemPosition) {
            0 -> TopicSortBy.LAST_UPDATED
            1 -> TopicSortBy.NEW_TOP
            else -> throw java.lang.IndexOutOfBoundsException()
        }

    private fun getSpinnerItemPositionOf(topicSortBy: TopicSortBy) =
        when (topicSortBy) {
            TopicSortBy.LAST_UPDATED -> 0
            TopicSortBy.NEW_TOP -> 1
            else -> throw java.lang.IndexOutOfBoundsException()
        }

    private fun onEnableGroupNotificationsCheckedInitializeOrChange(isChecked: Boolean) {
        allowDuplicateNotificationsPref.isEnabled = isChecked
        sortRecommendedPostsByTitle.isEnabled = isChecked
        sortRecommendedPostsBySpinner.isEnabled = isChecked
        feedRequestPostCountLimitTitle.isEnabled = isChecked
        feedRequestPostCountLimitEditText.isEnabled = isChecked
    }

    companion object {
        const val DIALOG_GROUP_TAB_NOTIFICATIONS_PREFERENCE =
            "dialog_group_tab_notifications_preference"
        private const val ARG_TAB_ID = "tab_id"
        fun newInstance(groupId: String): GroupTabNotificationsPreferenceDialogFragment {
            val bundle = Bundle().apply {
                putString(ARG_TAB_ID, groupId)
            }
            return GroupTabNotificationsPreferenceDialogFragment().apply { arguments = bundle }
        }
    }
}
