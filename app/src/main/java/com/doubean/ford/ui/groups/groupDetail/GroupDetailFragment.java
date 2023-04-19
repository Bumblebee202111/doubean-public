package com.doubean.ford.ui.groups.groupDetail;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.doubean.ford.R;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupTab;
import com.doubean.ford.data.vo.Resource;
import com.doubean.ford.data.vo.Status;
import com.doubean.ford.databinding.FragmentGroupDetailBinding;
import com.doubean.ford.util.InjectorUtils;
import com.doubean.ford.util.OpenInUtil;
import com.doubean.ford.util.ShareUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a single Group detail screen.
 */
public class GroupDetailFragment extends Fragment {

    String groupId;
    String defaultTabId;
    FragmentGroupDetailBinding binding;
    private GroupDetailViewModel groupDetailViewModel;
    private String shareText = "";

    public static int getItemOfId(List<GroupTab> groupTabs, String tabId) {
        if (tabId != null) {
            for (GroupTab tab : groupTabs) {
                if (Objects.equals(tab.id, tabId)) {
                    return tab.seq;
                }
            }
        }
        return 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentGroupDetailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        GroupDetailFragmentArgs args = GroupDetailFragmentArgs.fromBundle(requireArguments());
        groupId = args.getGroupId();
        defaultTabId = args.getDefaultTabId();

        GroupDetailViewModelFactory factory = InjectorUtils.provideGroupDetailViewModelFactory(requireContext(), groupId, defaultTabId);
        groupDetailViewModel = new ViewModelProvider(this, factory).get(GroupDetailViewModel.class);
        binding.setViewModel(groupDetailViewModel);

        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(), group -> {

            if (group != null && group.data != null && (group.status != Status.LOADING)) {

                this.shareText = group.data.name + ' ' + group.data.url + "\r\n";

                binding.setGroup(group.data);

                final int color = group.data.getColor();
                if (color != 0) {
                    binding.mask.setBackgroundColor(color);
                    binding.toolbarLayout.setContentScrimColor(color);
                    binding.toolbarLayout.setStatusBarScrimColor(color);
                    //getActivity().getWindow().setStatusBarColor(color);
                    binding.tabLayout.setSelectedTabIndicatorColor(color);
                }

                ViewPager2 viewPager = binding.pager;
                GroupPagerAdapter groupPagerAdapter = new GroupPagerAdapter(getChildFragmentManager(), getViewLifecycleOwner().getLifecycle(), group.data);
                viewPager.setAdapter(groupPagerAdapter);
                TabLayout tabLayout = binding.tabLayout;
                new TabLayoutMediator(tabLayout, viewPager,
                        (tab, position) -> {
                            tab.setText(position == 0 ? getString(R.string.all) : group.data.tabs.get(position - 1).name);
                        }
                ).attach();
                viewPager.setCurrentItem(getItemOfId(group.data.tabs, defaultTabId), false);

                int colorSurface = getColorSurface();
                groupDetailViewModel.getFollowed().observe(getViewLifecycleOwner(), followed -> {
                    MenuItem followedItem = binding.toolbar.getMenu().findItem(R.id.action_follow);

                    followedItem.setIcon(followed ? R.drawable.ic_remove : R.drawable.ic_add);
                    MaterialButton followUnfollow = binding.followUnfollow;
                    if (followed) {
                        followUnfollow.setIconResource(R.drawable.ic_remove);
                        followUnfollow.setText(R.string.unfollow);
                        followUnfollow.setIconTint(ColorStateList.valueOf(color));
                        followUnfollow.setTextColor(color);
                        followUnfollow.setBackgroundColor(colorSurface);
                    } else {
                        followUnfollow.setIconResource(R.drawable.ic_add);
                        followUnfollow.setText(R.string.follow);
                        followUnfollow.setIconTint(ColorStateList.valueOf(colorSurface));
                        followUnfollow.setTextColor(colorSurface);
                        followUnfollow.setBackgroundColor(color);
                    }
                });

            }

        });

        binding.followUnfollow.setOnClickListener(v -> {
            Boolean followed = groupDetailViewModel.getFollowed().getValue();
            if (followed != null) {
                if (followed) {
                    groupDetailViewModel.removeFollow();
                    Snackbar.make(binding.getRoot(), R.string.unfollowed_group, Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    groupDetailViewModel.addFollow();
                    Snackbar.make(binding.getRoot(), R.string.followed_group, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        binding.toolbarLayout.setCollapsedTitleTextColor(getContext().getColor(R.color.doubean_white));
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            //ref: https://stackoverflow.com/a/33891727
            boolean shouldShowToolbar = verticalOffset + appBarLayout.getTotalScrollRange() == 0;
            binding.appbar.setActivated(shouldShowToolbar);
        });
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        setHasOptionsMenu(true);

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return binding.getRoot();
    }

    private int getColorSurface() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorSurface, typedValue, true);
        return typedValue.data;
    }

    private boolean onMenuItemClick(MenuItem item) {
        Resource<GroupDetail> groupResource = groupDetailViewModel.getGroup().getValue();
        if (groupResource != null && groupResource.data != null) {
            GroupDetail group = groupResource.data;
            switch (item.getItemId()) {
                case R.id.action_follow:
                    Boolean isFollowed = groupDetailViewModel.getFollowed().getValue();
                    if (isFollowed != null) {
                        if (isFollowed) {
                            groupDetailViewModel.removeFollow();
                            Snackbar.make(binding.getRoot(), R.string.unfollowed_group, Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            groupDetailViewModel.addFollow();
                            Snackbar.make(binding.getRoot(), R.string.followed_group, Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                    return true;
                case R.id.action_share:
                    ShareUtil.Share(getContext(), shareText);
                    return true;
                case R.id.action_view_in_douban:
                    if (group.uri != null) {
                        String urlString = group.uri;
                        OpenInUtil.openInDouban(getContext(), urlString);
                    }
                    return true;
                case R.id.action_view_in_browser:
                    if (group.url != null) {
                        String urlString = group.url;
                        OpenInUtil.openInBrowser(getContext(), urlString);
                    }
                    return true;
            }
        }
        return false;
    }
}