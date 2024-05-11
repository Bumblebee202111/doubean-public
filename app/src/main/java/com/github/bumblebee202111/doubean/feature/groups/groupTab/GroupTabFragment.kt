package com.github.bumblebee202111.doubean.feature.groups.groupTab

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.compose.content
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemPostBinding
import com.github.bumblebee202111.doubean.databinding.ViewGroupTabActionsBinding
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailFragmentDirections
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailViewModel
import com.github.bumblebee202111.doubean.model.PostItem
import com.github.bumblebee202111.doubean.model.PostSortBy
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.getColorFromTheme
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupTabFragment : Fragment() {

    private val groupDetailViewModel: GroupDetailViewModel by viewModels({ requireParentFragment() })
    var tabId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        val groupTabViewModel: GroupTabViewModel = viewModel()


        tabId = groupTabViewModel.tabId

        val group by groupDetailViewModel.group.collectAsStateWithLifecycle()

        val topicLazyPagingItems = groupTabViewModel.topicsPagingData.collectAsLazyPagingItems()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(rememberNestedScrollInteropConnection())
        ) {

            item(
                key = "tab_actions", contentType = "tab_actions"
            ) {
                AndroidViewBinding(
                    factory = { inflater, parent, attachToParent ->
                        ViewGroupTabActionsBinding.inflate(
                            inflater,
                            parent,
                            attachToParent
                        ).apply {
                            fun setupSpinner() {
                                val arrayAdapter = ArrayAdapter.createFromResource(
                                    requireContext(),
                                    R.array.sort_posts_by_array,
                                    android.R.layout.simple_spinner_item
                                )
                                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                                sortPostsBySpinner.adapter = arrayAdapter
                                sortPostsBySpinner.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            // A Kotlin bug: https://stackoverflow.com/questions/48343622/how-to-fix-parameter-specified-as-non-null-is-null-on-rotating-screen-in-a-fragm
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
                            setupSpinner()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    fun setupFollowUnFollowAndMore() {

                        if (tabId == null) { //All
                            toggleGroup.visibility = View.GONE
                            more.visibility = View.GONE
                        } else { //Non-all tab
                            followUnfollow.setOnClickListener {
                                group?.findTab(tabId)?.let { tab ->
                                    if (tab.isFollowed) {
                                        groupTabViewModel.removeFollow()
                                        view?.showSnackbar(
                                            R.string.unfollowed_tab,
                                            Snackbar.LENGTH_LONG
                                        )
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

                            notificationButton.setOnClickListener {
                                showNotificationsPrefDialog()
                            }

                            more.setOnClickListener { v ->
                                val popup = PopupMenu(requireContext(), v)
                                popup.setOnMenuItemClickListener { item ->
                                    when (item.itemId) {
                                        R.id.action_share -> {
                                            val shareText = StringBuilder()
                                            groupDetailViewModel.group.value?.let { group ->
                                                shareText.append(group.name + "|")
                                                if (tabId != null)
                                                    group.tabs?.first { it.id == tabId }
                                                        ?.let { tab ->
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

                    setupFollowUnFollowAndMore()

                    group?.let { group ->
                        val colorSurface = requireContext().getColorFromTheme(R.attr.colorSurface)
                        val groupColor =
                            group.color ?: requireContext().getColorFromTheme(R.attr.colorPrimary)
                        notificationButton.visibility =
                            if (group.findTab(tabId)?.enableNotifications == null) View.GONE else View.VISIBLE
                        group.findTab(tabId)?.let { tab ->
                            with(followUnfollow) {
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

                            with(notificationButton) {
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
            }

            items(
                count = topicLazyPagingItems.itemCount,
                key = topicLazyPagingItems.itemKey { it.id },
                contentType = topicLazyPagingItems.itemContentType { "topicLazyPagingItem" }
            ) { index ->
                val topic = topicLazyPagingItems[index]
                AndroidViewBinding(factory = ListItemPostBinding::inflate,
                    modifier = Modifier.fillMaxWidth(),
                    onReset = {}
                ) {
                    post = topic
                    executePendingBindings()

                    clickListener =
                        View.OnClickListener { topic?.let { navigateToPost(it) } }
                    showPopup = View.OnClickListener { v ->
                        val popupMenu = PopupMenu(v.context, more)
                        popupMenu.inflate(R.menu.menu_post_item)
                        popupMenu.setOnMenuItemClickListener { item ->
                            post?.let {
                                when (item.itemId) {
                                    R.id.action_share -> {
                                        val context = v.context
                                        val shareText = StringBuilder()
                                        group?.let { group ->
                                            shareText.append(group.name + "|")
                                        }

                                        it.tag?.let { tag ->
                                            shareText.append(tag.name)
                                        }
                                        shareText.append("@${it.author.name}${context.getString(R.string.colon)} ${it.title} ${it.url}")
                                        ShareUtil.share(context, shareText)
                                        false
                                    }

                                    else -> false
                                }
                            }
                            false
                        }
                        popupMenu.show()
                    }

                    authorAvatar.setContent {
                        UserProfileImage(
                            url = topic?.author?.avatarUrl,
                            size = dimensionResource(id = R.dimen.icon_size_extra_small)
                        )
                    }

                }
                if (index != topicLazyPagingItems.itemCount - 1) {
                    HorizontalDivider()
                }

            }
        }
    }

    private fun navigateToPost(post: PostItem) {
        val direction = GroupDetailFragmentDirections.actionGroupDetailToPostDetail(post.id)
        findNavController().navigate(direction)
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
        GroupTabNotificationsPreferenceDialogFragment.newInstance(tabId!!)
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