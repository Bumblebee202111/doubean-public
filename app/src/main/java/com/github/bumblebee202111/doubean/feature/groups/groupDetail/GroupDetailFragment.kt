package com.github.bumblebee202111.doubean.feature.groups.groupDetail

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
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.FragmentGroupDetailBinding
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupNotificationsPreferenceDialogFragment.Companion.DIALOG_GROUP_NOTIFICATIONS_PREFERENCE
import com.github.bumblebee202111.doubean.model.GroupTab
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.github.bumblebee202111.doubean.util.OpenInUtil
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.getColorFromTheme
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupDetailFragment : Fragment() {
    private val args: GroupDetailFragmentArgs by navArgs()
    private lateinit var groupId: String
    private var defaultSelectedTabId: String? = null

    private lateinit var binding: FragmentGroupDetailBinding
    private val groupDetailViewModel: GroupDetailViewModel by viewModels()
    private lateinit var viewPager: ViewPager2

    private lateinit var groupPagerAdapter: GroupPagerAdapter

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
            groupDetailViewModel.group.value?.isFollowed?.let(::onFollowGroup)
        }

        binding.notificationButton.setOnClickListener { showNotificationsPrefDialog() }

        binding.toolbarLayout.setCollapsedTitleTextColor(requireContext().getColor(R.color.doubean_white))
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            
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
            it?.let { group ->
                binding.group = group
                group.color?.let { color ->
                    binding.mask.setBackgroundColor(color)
                    binding.toolbarLayout.setContentScrimColor(color)
                    binding.toolbarLayout.setStatusBarScrimColor(color)
                    binding.tabLayout.setSelectedTabIndicatorColor(color)
                }


                val colorSurface = requireContext().getColorFromTheme(R.attr.colorSurface)
                val groupColor =
                    group.color ?: requireContext().getColorFromTheme(R.attr.colorPrimary)

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
        repeatWithViewLifecycle {
            launch {
                groupDetailViewModel.tabs.collect { tabs ->
                    val taggedTabIds = tabs?.map(GroupTab::id)

                    groupPagerAdapter =
                        GroupPagerAdapter(
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
                            else groupDetailViewModel.group.value!!.tabs!![position - 1].name
                    }.attach()

                }
            }
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        groupDetailViewModel.group.value?.let { group ->
            when (item.itemId) {
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
        GroupNotificationsPreferenceDialogFragment.newInstance(
            groupId
        )
            .show(childFragmentManager, DIALOG_GROUP_NOTIFICATIONS_PREFERENCE)
    }

    companion object {
        private fun getItemOfTabId(groupTabs: List<GroupTab>, tabId: String?) =
            groupTabs.firstOrNull { it.id == tabId }?.seq ?: 0
    }
}