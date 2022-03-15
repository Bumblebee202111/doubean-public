package com.doubean.ford.ui.groupDetail;

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
import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupTopicTag;
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

    String groupId;
    String defaultTabId;
    private GroupDetailViewModel groupDetailViewModel;

    public static GroupDetailFragment newInstance() {
        return new GroupDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentGroupDetailBinding binding = FragmentGroupDetailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupDetailFragmentArgs args = GroupDetailFragmentArgs.fromBundle(requireArguments());
        groupId = args.getGroupId();
        defaultTabId = args.getDefaultTabId();
        GroupDetailViewModelFactory factory = InjectorUtils.provideGroupDetailViewModelFactory(requireContext(), groupId, defaultTabId);
        groupDetailViewModel = new ViewModelProvider(this, factory).get(GroupDetailViewModel.class);
        binding.setViewModel(groupDetailViewModel);
        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(),
                group -> {
                    binding.setGroup(group);
                    binding.appbar.setBackgroundColor(Color.parseColor(group == null ? "#FFFFFF" : group.getColor()));
                    if (group != null && group.groupTabs != null) {

                        List<GroupTopicTag> groupTopicTags = group.getGroupTabs();
                        GroupPagerAdapter groupPagerAdapter = new GroupPagerAdapter(GroupDetailFragment.this, groupId, group.getGroupTabs());

                        /*Hacking ViewPager2*/
                        ViewPager2 viewPager = binding.pager;
                        presetDefaultItem(viewPager, group);
                        viewPager.setAdapter(groupPagerAdapter);

                        TabLayout tabLayout = binding.tabLayout;
                        new TabLayoutMediator(tabLayout, viewPager,
                                (tab, position) -> {
                                    tab.setText(position == 0 ? getString(R.string.all) : groupTopicTags.get(position - 1).name);
                                }
                        ).attach();

                        /*TODO: remove below code, perform tab/page operations inside the page instead */

                        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                groupDetailViewModel.setCurrentTabId(position == 0 ? null : group.groupTabs.get(position - 1).id);
                            }
                        });

                    }
                });
        groupDetailViewModel.getCurrentTabFavorite().observe(getViewLifecycleOwner(), aBoolean -> {
            MenuItem favoriteItem = binding.toolbar.getMenu().findItem(R.id.action_favorite);
            favoriteItem.setIcon(aBoolean ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        });

        /*
          Below code doesn't make visibility of toolbar adaptive because setOnScrollChangeListener
          must be called on a NestedScrollView.
          Possible fix: passing a callback to tab adapter
        final Boolean[] isToolbarShown = {false};
        binding.pager.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    boolean shouldShowToolbar = scrollY > binding.toolbar.getHeight();
                    if (isToolbarShown[0] != shouldShowToolbar) {
                        isToolbarShown[0] = shouldShowToolbar;
                        binding.appbar.setActivated(shouldShowToolbar);
                        binding.toolbarLayout.setTitleEnabled(shouldShowToolbar);
                    }
                });
        */

        binding.toolbar.setOnMenuItemClickListener(item -> {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case R.id.action_favorite:
                    Boolean isFavorite = groupDetailViewModel.getCurrentTabFavorite().getValue();
                    if (isFavorite != null) {
                        if (isFavorite) {
                            groupDetailViewModel.removeFavorite();
                            Snackbar.make(binding.getRoot(), R.string.removed_group_tab_from_favorites, Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            groupDetailViewModel.addFavorite();
                            Snackbar.make(binding.getRoot(), R.string.added_group_tab_to_favorites, Snackbar.LENGTH_LONG)
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

    private int getDefaultItem(Group group) {
        if (defaultTabId != null) {
            for (int i = 0; i < group.groupTabs.size(); i++) {
                if (Objects.equals(group.groupTabs.get(i).id, defaultTabId)) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    private void presetDefaultItem(ViewPager2 viewPager, Group group) {
        try {
            Field field = viewPager.getClass().getDeclaredField("mPendingCurrentItem");
            field.setAccessible(true);
            try {
                if (field.getInt(viewPager) == -1) { //-1: NO_POSITION
                    field.setInt(viewPager, getDefaultItem(group));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}