package com.doubean.ford.ui.groups.groupDetail;

import android.graphics.Color;
import android.os.Bundle;
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
import com.doubean.ford.databinding.FragmentGroupDetailBinding;
import com.doubean.ford.util.InjectorUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a single Group detail screen.
 */
public class GroupDetailFragment extends Fragment {

    final Boolean[] isToolbarShown = {false};
    String groupId;
    String defaultTabId;
    FragmentGroupDetailBinding binding;
    private GroupDetailViewModel groupDetailViewModel;

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
            if (group != null) {
                binding.setGroup(group);

                final int color = group.getColor();
                binding.mask.setBackgroundColor(color);
                binding.toolbar.setBackgroundColor(color);
                binding.tabLayout.setSelectedTabIndicatorColor(color);

                List<GroupTab> tabs = group.tabs;

                GroupPagerAdapter groupPagerAdapter = new GroupPagerAdapter(GroupDetailFragment.this, groupId, tabs);

                ViewPager2 viewPager = binding.pager;
                initViewPager2WithDefaultItem(viewPager, group);
                viewPager.setAdapter(groupPagerAdapter);

                TabLayout tabLayout = binding.tabLayout;
                new TabLayoutMediator(tabLayout, viewPager,
                        (tab, position) -> {
                            tab.setText(position == 0 ? getString(R.string.all) : tabs.get(position - 1).name);
                        }
                ).attach();

                /*TODO: remove below code, perform tab/page operations inside the page instead */
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        groupDetailViewModel.setCurrentTabId(position == 0 ? null : tabs.get(position - 1).id);
                    }
                });
            }

        });
        groupDetailViewModel.getCurrentTabFollowed().observe(getViewLifecycleOwner(), followed -> {
            MenuItem followedItem = binding.toolbar.getMenu().findItem(R.id.action_follow);
            followedItem.setIcon(followed ? R.drawable.ic_remove : R.drawable.ic_add);
        });
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            //ref: https://stackoverflow.com/a/33891727
            boolean shouldShowToolbar = verticalOffset + appBarLayout.getTotalScrollRange() == 0;

            if (isToolbarShown[0] != shouldShowToolbar) {
                isToolbarShown[0] = shouldShowToolbar;
                binding.appbar.setActivated(shouldShowToolbar);
                binding.toolbarLayout.setTitleEnabled(shouldShowToolbar);
            }

        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case R.id.action_follow:
                    Boolean isFollowed = groupDetailViewModel.getCurrentTabFollowed().getValue();
                    if (isFollowed != null) {
                        if (isFollowed) {
                            groupDetailViewModel.removeFollowed();
                            Snackbar.make(binding.getRoot(), R.string.removed_group_tab_from_followed, Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            groupDetailViewModel.addFollowed();
                            Snackbar.make(binding.getRoot(), R.string.added_group_tab_to_followed, Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }

                    return true;
                default:
                    return false;
            }
        });
        setHasOptionsMenu(true);

        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(), group -> {
            if (group != null) {
                getActivity().getWindow().setStatusBarColor(group.getColor());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private int getDefaultItem(List<GroupTab> groupTabs) {
        if (defaultTabId != null) {
            for (GroupTab tab : groupTabs) {
                if (Objects.equals(tab.id, defaultTabId)) {
                    return tab.seq;
                }
            }
        }
        return 0;
    }

    private void initViewPager2WithDefaultItem(ViewPager2 viewPager, GroupDetail group) {
        try {
            Field field = viewPager.getClass().getDeclaredField("mPendingCurrentItem");
            field.setAccessible(true);
            try {
                if (field.getInt(viewPager) == -1) { //-1: NO_POSITION
                    field.setInt(viewPager, getDefaultItem(group.tabs));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}