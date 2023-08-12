package com.doubean.ford.ui.groups.groupDetail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.doubean.ford.R
import com.doubean.ford.databinding.FragmentGroupDetailBinding
import com.doubean.ford.model.GroupTab
import com.doubean.ford.ui.groups.groupDetail.GroupNotificationsPreferenceDialogFragment.Companion.DIALOG_GROUP_NOTIFICATIONS_PREFERENCE
import com.doubean.ford.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A fragment representing a single Group detail screen.
 */
class GroupDetailFragment : Fragment() {
    private val args: GroupDetailFragmentArgs by navArgs()
    private lateinit var groupId: String
    private var defaultSelectedTabId: String? = null

    private lateinit var binding: FragmentGroupDetailBinding
    private val groupDetailViewModel: GroupDetailViewModel by viewModels {
        InjectorUtils.provideGroupDetailViewModelFactory(
            requireContext(),
            groupId,
            defaultSelectedTabId
        )
    }
    private lateinit var viewPager: ViewPager2

    lateinit var groupPagerAdapter: GroupPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        groupId = args.groupId
        defaultSelectedTabId = args.defaultTabId
        binding = FragmentGroupDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = groupDetailViewModel
        }

        viewPager = binding.pager

        binding.followUnfollow.setOnClickListener {
            groupDetailViewModel.group.value?.data?.isFollowed?.let(::onFollowGroup)
        }

        binding.notificationButton.setOnClickListener { showNotificationsPrefDialog() }

        binding.toolbarLayout.setCollapsedTitleTextColor(requireContext().getColor(R.color.doubean_white))
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            //ref: https://stackoverflow.com/a/33891727
            val shouldShowToolbar = verticalOffset + appBarLayout.totalScrollRange == 0
            binding.appbar.isActivated = shouldShowToolbar
        }
        binding.toolbar.setOnMenuItemClickListener(::onMenuItemClick)
        setHasOptionsMenu(true)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupDetailViewModel.group.observe(viewLifecycleOwner) {
            it?.data?.let { group ->
                binding.group = group
                group.color?.let { color ->
                    binding.mask.setBackgroundColor(color)
                    binding.toolbarLayout.setContentScrimColor(color)
                    binding.toolbarLayout.setStatusBarScrimColor(color)
                    binding.tabLayout.setSelectedTabIndicatorColor(color)
                }
                group.tabs?.let { tabs ->
                    val taggedTabIds = tabs.map(GroupTab::id)
                    if (viewPager.adapter == null && (defaultSelectedTabId == null || taggedTabIds.contains(
                            defaultSelectedTabId
                        ))
                    ) {
                        groupPagerAdapter = GroupPagerAdapter(
                            childFragmentManager,
                            viewLifecycleOwner.lifecycle,
                            groupId,
                            taggedTabIds
                        )
                        viewPager.adapter = groupPagerAdapter
                        val tabLayout = binding.tabLayout
                        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                            tab.text =
                                if (position == 0) getString(R.string.all)
                                else groupDetailViewModel.group.value!!.data!!.tabs!![position - 1].name
                        }.attach()
                        groupDetailViewModel.pagerPreselectedEvent.observe(viewLifecycleOwner,
                            EventObserver {
                                viewPager.setCurrentItem(
                                    getItemOfTabId(tabs, defaultSelectedTabId),
                                    false
                                )
                            })
                    } else if (viewPager.adapter != null) {
                        groupPagerAdapter.taggedTabIds = taggedTabIds

                    }
                    viewPager.offscreenPageLimit = tabs.size + 1
                }

                val colorSurface = requireContext().getColorFromTheme(R.attr.colorSurface)
                val groupColor =
                    group.color ?: requireContext().getColorFromTheme(R.attr.colorPrimary)

                val followedItem = binding.toolbar.menu.findItem(R.id.action_follow)
                followedItem.setIcon(if (group.isFollowed) R.drawable.ic_remove else R.drawable.ic_add)

                with(binding.followUnfollow) {
                    if (group.isFollowed) {
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
                    when (group.enableNotifications) {
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

    private fun onMenuItemClick(item: MenuItem): Boolean {
        groupDetailViewModel.group.value?.data?.let { group ->
            when (item.itemId) {
                R.id.action_follow -> {
                    onFollowGroup(group.isFollowed)
                    return true
                }
                R.id.action_share -> {
                    val shareText = group.name + ' ' + group.shareUrl + "\r\n"
                    ShareUtil.share(requireContext(), shareText)
                    return true
                }
                R.id.action_view_in_douban -> {
                    val urlString: String = group.uri
                    OpenInUtil.openInDouban(requireContext(), urlString)
                    return true
                }
                R.id.action_view_in_browser -> {
                    val urlString: String = group.shareUrl
                    OpenInUtil.openInBrowser(requireContext(), urlString)
                    return true
                }
                else -> {}
            }
        }
        return false
    }

    private fun onFollowGroup(oldIsFollowed: Boolean) {
        if (oldIsFollowed) {
            groupDetailViewModel.removeFollow()
            view?.showSnackbar(
                R.string.unfollowed_group,
                Snackbar.LENGTH_LONG,
            )
        } else {
            groupDetailViewModel.addFollow()
            view?.showSnackbar(
                R.string.followed_group, Snackbar.LENGTH_LONG, R.string.edit_follow_preferences
            ) { showNotificationsPrefDialog() }
        }
    }

    private fun showNotificationsPrefDialog() {
        GroupNotificationsPreferenceDialogFragment.newInstance(groupId)
            .show(childFragmentManager, DIALOG_GROUP_NOTIFICATIONS_PREFERENCE)
    }


    companion object {
        private fun getItemOfTabId(groupTabs: List<GroupTab>, tabId: String?) =
            groupTabs.firstOrNull { it.id == tabId }?.seq ?: 0
    }
}