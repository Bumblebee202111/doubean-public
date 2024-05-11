package com.github.bumblebee202111.doubean.feature.groups.groupDetail

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.viewModels
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.DialogGroupNotificationsPreferenceBinding
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.util.MinMaxEditTextInputFilter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class GroupNotificationsPreferenceDialogFragment : AppCompatDialogFragment() {

    private val groupDetailViewModel: GroupDetailViewModel by viewModels({ requireParentFragment() })
    lateinit var binding: DialogGroupNotificationsPreferenceBinding
    private lateinit var enableGroupNotificationsPref: SwitchMaterial
    private lateinit var allowDuplicateNotificationsPref: SwitchMaterial
    private lateinit var sortRecommendedPostsByTitle: MaterialTextView
    private lateinit var sortRecommendedPostsBySpinner: AppCompatSpinner
    private lateinit var feedRequestPostCountLimitTitle: MaterialTextView
    private lateinit var feedRequestPostCountLimitEditText: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding =
            DialogGroupNotificationsPreferenceBinding.inflate(requireActivity().layoutInflater)
                .apply {
                    viewModel = groupDetailViewModel
                    lifecycleOwner = this@GroupNotificationsPreferenceDialogFragment
                }

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

        binding.feedRequestPostCountLimitEditText.filters =
            arrayOf(MinMaxEditTextInputFilter(1, 50))

        groupDetailViewModel.group.observe(this) { group ->
            group?.sortRecommendedPostsBy?.let {
                sortRecommendedPostsBySpinner.setSelection(getSpinnerItemPositionOf(it))
            }
        }

        return MaterialAlertDialogBuilder(requireContext()).setView(binding.root)
            .setTitle(getString(R.string.group_notifications_preference))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                groupDetailViewModel.saveNotificationsPreference(
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
            0 -> PostSortBy.LAST_UPDATED
            1 -> PostSortBy.NEW_TOP
            else -> throw java.lang.IndexOutOfBoundsException()
        }

    private fun getSpinnerItemPositionOf(postSortBy: PostSortBy) =
        when (postSortBy) {
            PostSortBy.LAST_UPDATED -> 0
            PostSortBy.NEW_TOP -> 1
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
        const val DIALOG_GROUP_NOTIFICATIONS_PREFERENCE = "dialog_group_notifications_preference"
        private const val ARG_GROUP_ID = "group_id"
        fun newInstance(groupId: String): GroupNotificationsPreferenceDialogFragment {
            val bundle = Bundle().apply {
                putString(ARG_GROUP_ID, groupId)
            }
            return GroupNotificationsPreferenceDialogFragment().apply { arguments = bundle }
        }
    }
}


