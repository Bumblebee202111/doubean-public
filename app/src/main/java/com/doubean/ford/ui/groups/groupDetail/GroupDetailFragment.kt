package com.doubean.ford.ui.groups.groupDetail

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.doubean.ford.R
import com.doubean.ford.data.vo.GroupDetail
import com.doubean.ford.data.vo.GroupTab
import com.doubean.ford.data.vo.Resource
import com.doubean.ford.data.vo.Status
import com.doubean.ford.databinding.FragmentGroupDetailBinding
import com.doubean.ford.util.InjectorUtils
import com.doubean.ford.util.OpenInUtil
import com.doubean.ford.util.ShareUtil
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A fragment representing a single Group detail screen.
 */
class GroupDetailFragment : Fragment() {
    private lateinit var groupId: String
    var defaultTabId: String? = null
    private lateinit var binding: FragmentGroupDetailBinding
    private lateinit var groupDetailViewModel: GroupDetailViewModel
    private var shareText = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val args = GroupDetailFragmentArgs.fromBundle(requireArguments())
        groupId = args.groupId
        defaultTabId = args.defaultTabId
        val factory: GroupDetailViewModelFactory = InjectorUtils.provideGroupDetailViewModelFactory(
            requireContext(),
            groupId,
            defaultTabId
        )
        groupDetailViewModel = ViewModelProvider(this, factory).get(
            GroupDetailViewModel::class.java
        )
        binding.viewModel = groupDetailViewModel
        groupDetailViewModel.group
            .observe(viewLifecycleOwner) { group: Resource<GroupDetail?>? ->
                if (group != null && group.data != null && group.status !== Status.LOADING) {
                    shareText = group.data.name + ' ' + group.data.url + "\r\n"
                    binding.group = group.data
                    val color: Int = group.data.color
                    if (color != 0) {
                        binding.mask.setBackgroundColor(color)
                        binding.toolbarLayout.setContentScrimColor(color)
                        binding.toolbarLayout.setStatusBarScrimColor(color)
                        binding.tabLayout.setSelectedTabIndicatorColor(color)
                    }
                    val viewPager: ViewPager2 = binding.pager
                    val groupPagerAdapter = GroupPagerAdapter(
                        childFragmentManager,
                        viewLifecycleOwner.lifecycle,
                        group.data
                    )
                    viewPager.adapter = groupPagerAdapter
                    val tabLayout: TabLayout = binding.tabLayout
                    TabLayoutMediator(
                        tabLayout, viewPager
                    ) { tab: TabLayout.Tab, position: Int ->
                        tab.text =
                            if (position == 0) getString(R.string.all) else group.data.tabs?.get(position - 1)?.name
                    }.attach()
                    group.data.tabs?.let { it -> getItemOfId(it, defaultTabId)?.let { viewPager.setCurrentItem(it, false) } }
                    val colorSurface = colorSurface
                    groupDetailViewModel.followed
                        .observe(viewLifecycleOwner) { followed: Boolean? ->
                            val followedItem = binding.toolbar.menu.findItem(R.id.action_follow)
                            followedItem.setIcon(if (followed!!) R.drawable.ic_remove else R.drawable.ic_add)
                            val followUnfollow: MaterialButton = binding.followUnfollow
                            if (followed) {
                                followUnfollow.setIconResource(R.drawable.ic_remove)
                                followUnfollow.setText(R.string.unfollow)
                                followUnfollow.setIconTint(ColorStateList.valueOf(color))
                                followUnfollow.setTextColor(color)
                                followUnfollow.setBackgroundColor(colorSurface)
                            } else {
                                followUnfollow.setIconResource(R.drawable.ic_add)
                                followUnfollow.setText(R.string.follow)
                                followUnfollow.setIconTint(ColorStateList.valueOf(colorSurface))
                                followUnfollow.setTextColor(colorSurface)
                                followUnfollow.setBackgroundColor(color)
                            }
                        }
                }
            }
        binding.followUnfollow.setOnClickListener {
            val followed = groupDetailViewModel.followed.value
            if (followed != null) {
                if (followed) {
                    groupDetailViewModel.removeFollow()
                    Snackbar.make(binding.root, R.string.unfollowed_group, Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    groupDetailViewModel.addFollow()
                    Snackbar.make(binding.root, R.string.followed_group, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
        binding.toolbarLayout.setCollapsedTitleTextColor(requireContext().getColor(R.color.doubean_white))
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            //ref: https://stackoverflow.com/a/33891727
            val shouldShowToolbar = verticalOffset + appBarLayout.totalScrollRange == 0
            binding.appbar.isActivated = shouldShowToolbar
        })
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem -> onMenuItemClick(item) }
        setHasOptionsMenu(true)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        return binding.root

    }

    private val colorSurface: Int
        private get() {
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorSurface, typedValue, true)
            return typedValue.data
        }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        val groupResource: Resource<GroupDetail?>? = groupDetailViewModel.group.value
        if (groupResource?.data != null) {
            val group: GroupDetail = groupResource.data
            when (item.itemId) {
                R.id.action_follow -> {
                    val isFollowed = groupDetailViewModel.followed.value
                    if (isFollowed != null) {
                        if (isFollowed) {
                            groupDetailViewModel.removeFollow()
                            Snackbar.make(
                                binding.root,
                                R.string.unfollowed_group,
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        } else {
                            groupDetailViewModel.addFollow()
                            Snackbar.make(
                                binding.root,
                                R.string.followed_group,
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                    return true
                }
                R.id.action_share -> {
                    ShareUtil.Share(requireContext(), shareText)
                    return true
                }
                R.id.action_view_in_douban -> {
                    val urlString: String = group.uri
                    OpenInUtil.openInDouban(requireContext(), urlString)
                    return true
                }
                R.id.action_view_in_browser -> {
                    val urlString: String = group.url
                    OpenInUtil.openInBrowser(requireContext(), urlString)
                    return true
                }
            }
        }
        return false
    }

    companion object {
        fun getItemOfId(groupTabs: List<GroupTab>, tabId: String?) =
            groupTabs.firstOrNull {
                it.id == tabId
            }?.seq
    }
}