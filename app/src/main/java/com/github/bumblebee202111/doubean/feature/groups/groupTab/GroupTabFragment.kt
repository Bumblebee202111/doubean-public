package com.github.bumblebee202111.doubean.feature.groups.groupTab

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentGroupTabBinding
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailViewModel
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.ui.common.RetryCallback
import com.github.bumblebee202111.doubean.ui.common.bindResult
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.getColorFromTheme
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupTabFragment : Fragment() {
    lateinit var binding: FragmentGroupTabBinding
    private val args by lazy { requireArguments() }
    lateinit var groupId: String
    var tagId: String? = null
    val groupTabViewModel: GroupTabViewModel by viewModels()
    private val groupDetailViewModel: GroupDetailViewModel by viewModels({ requireParentFragment() })
    private lateinit var followUnfollow: MaterialButton
    private lateinit var more: AppCompatImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        groupId = args.getString(ARG_GROUP_ID)!!
        tagId = args.getString(ARG_TAG_ID)
        binding = FragmentGroupTabBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFollowUnFollowAndMore()

        setupSpinner()

        groupDetailViewModel.group.observe(viewLifecycleOwner) {
            it.data?.let { group ->
                val colorSurface = requireContext().getColorFromTheme(R.attr.colorSurface)
                val groupColor =
                    group.color ?: requireContext().getColorFromTheme(R.attr.colorPrimary)
                binding.notificationButton.visibility =
                    if (group.findTab(tagId)?.enableNotifications == null) View.GONE else View.VISIBLE
                group.findTab(tagId)?.let { tab ->
                    with(binding.followUnfollow) {
                        if (tab.isFollowed) {
                            setIconResource(R.drawable.ic_remove)
                            setText(R.string.unfollow)
                            iconTint = ColorStateList.valueOf(groupColor)
                            setTextColor(groupColor)
                            setBackgroundColor(colorSurface)
                        } else {
                            setIconResource(R.drawable.ic_add)
                            setText(R.string.follow)
                            iconTint = ColorStateList.valueOf(colorSurface)
                            setTextColor(colorSurface)
                            setBackgroundColor(groupColor)
                        }
                    }

                    with(binding.notificationButton) {
                        when (tab.enableNotifications) {
                            true -> {
                                setIconResource(R.drawable.ic_notifications)
                                iconTint = ColorStateList.valueOf(groupColor)
                                setBackgroundColor(colorSurface)
                            }
                            false -> {
                                setIconResource(R.drawable.ic_notification_add)
                                iconTint = ColorStateList.valueOf(colorSurface)
                                setBackgroundColor(groupColor)
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }

        initPostList()
    }

    private fun setupFollowUnFollowAndMore() {
        followUnfollow = binding.followUnfollow
        more = binding.more
        if (tagId == null) { 
            binding.toggleGroup.visibility = View.GONE
            binding.more.visibility = View.GONE
        } else { 
            followUnfollow.setOnClickListener {
                groupDetailViewModel.group.value?.data?.findTab(tagId)?.let { tab ->
                    if (tab.isFollowed) {
                        groupTabViewModel.removeFollow()
                        view?.showSnackbar(R.string.unfollowed_tab, Snackbar.LENGTH_LONG)
                    } else {
                        groupTabViewModel.addFollow()
                        view?.showSnackbar(
                            R.string.followed_tab,
                            Snackbar.LENGTH_LONG,
                            R.string.edit_follow_preferences
                        ) { showNotificationsPrefDialog() }
                    }
                }
            }

            binding.notificationButton.setOnClickListener {
                showNotificationsPrefDialog()
            }

            more.setOnClickListener { v ->
                val popup = PopupMenu(requireContext(), v)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_share -> {
                            val shareText = StringBuilder()
                            groupDetailViewModel.group.value?.data?.let { group ->
                                shareText.append(group.name + "|")
                                if (tagId != null)
                                    group.tabs?.first { it.id == tagId }?.let { tab ->
                                        shareText.append(tab.name)
                                    }
                                shareText.append(' ' + group.shareUrl + "\r\n")
                                context?.let { ShareUtil.share(it, shareText) }
                                true
                            }

                        }
                    }
                    false
                }
                popup.inflate(R.menu.menu_group_tab)
                popup.show()
            }
        }


    }

    private fun initPostList() {
        binding.callback = object : RetryCallback {
            override fun retry() {
                groupTabViewModel.refreshPosts()
            }
        }
        binding.swiperefresh.setOnRefreshListener { groupTabViewModel.refreshPosts() }

        binding.scrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                groupTabViewModel.loadNextPage()
            }
        }

        binding.postList.addItemDecoration(
            DividerItemDecoration(binding.postList.context, DividerItemDecoration.VERTICAL)
        )
        val adapter = PostAdapter(groupDetailViewModel.group)
        binding.postList.adapter = adapter
        groupTabViewModel.postsResult.observe(viewLifecycleOwner) { result ->
            adapter.submitList(result?.data)
            if (result != null) binding.swiperefresh.isRefreshing = false
            binding.loadingState.bindResult(result)
        }
        groupTabViewModel.loadMoreStatus.observe(viewLifecycleOwner) { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
            binding.executePendingBindings()
        }
    }

    private fun setupSpinner() {
        val spinner = binding.sortPostsBySpinner
        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_posts_by_array, android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                
                view: View?,
                position: Int,
                id: Long,
            ) {
                groupTabViewModel.setSortBy(getSortByAt(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    private fun getSortByAt(position: Int): PostSortBy {
        return when (position) {
            0 -> PostSortBy.LAST_UPDATED
            1 -> PostSortBy.NEW
            2 -> PostSortBy.TOP
            else -> throw (IndexOutOfBoundsException())
        }
    }

    private fun showNotificationsPrefDialog() {
        GroupTabNotificationsPreferenceDialogFragment.newInstance(tagId!!)
            .show(
                childFragmentManager,
                GroupTabNotificationsPreferenceDialogFragment.DIALOG_GROUP_TAB_NOTIFICATIONS_PREFERENCE
            )
    }

    companion object {
        const val ARG_GROUP_ID = "group_id"
        const val ARG_TAG_ID = "tag_id"
        fun newInstance(
            groupId: String,
            tagId: String? = null,
        ): GroupTabFragment {
            val args = Bundle()
            args.putString(ARG_GROUP_ID, groupId)
            args.putString(ARG_TAG_ID, tagId)
            val fragment = GroupTabFragment()
            fragment.arguments = args
            return fragment
        }
    }
}