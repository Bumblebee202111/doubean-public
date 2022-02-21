package com.doubean.ford.ui.groupDetail;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.doubean.ford.R;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupTopicTag;
import com.doubean.ford.databinding.FragmentGroupDetailBinding;
import com.doubean.ford.util.InjectorUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a single Group detail screen.
 */
public class GroupDetailFragment extends Fragment {

    private GroupDetailViewModel groupDetailViewModel;
    FragmentGroupDetailBinding binding;
    String groupId;
    String defaultTabId;
    public static GroupDetailFragment newInstance() {
        return new GroupDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentGroupDetailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupDetailFragmentArgs args = GroupDetailFragmentArgs.fromBundle(requireArguments());
        groupId = args.getGroupId();
        defaultTabId = args.getDefaultTabId();
        GroupDetailViewModelFactory factory = InjectorUtils.provideGroupDetailViewModelFactory(requireContext(), groupId, defaultTabId);
        groupDetailViewModel = new ViewModelProvider(this, factory).get(GroupDetailViewModel.class);
        binding.setViewModel(groupDetailViewModel);
        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(@NonNull Group group) {
                binding.setGroup(group);
            }
        });
        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(),
                group -> binding.appbar.setBackgroundColor(Color.parseColor(group == null ? "#FFFFFF" : group.getColor())));

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.inflateMenu(R.menu.group_detail_menu);
        groupDetailViewModel.getCurrentTabFavorite().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                MenuItem favoriteItem = binding.toolbar.getMenu().findItem(R.id.action_favorite);
                favoriteItem.setIcon(aBoolean ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            }
        });
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
                        return true;
                }
            }
        });
        ViewPager2 viewPager = binding.pager;
        groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                if (group != null && group.groupTabs != null) {
                    List<GroupTopicTag> groupTopicTags = group.getGroupTabs();
                    GroupTabAdapter groupTabAdapter = new GroupTabAdapter(GroupDetailFragment.this, groupId, group.getGroupTabs());
                    viewPager.setAdapter(groupTabAdapter);
                    TabLayout tabLayout = binding.tabLayout;
                    new TabLayoutMediator(tabLayout, viewPager,
                            (tab, position) -> {
                                tab.setText(position == 0 ? getString(R.string.all) : groupTopicTags.get(position - 1).name);
                            }
                    ).attach();

                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            groupDetailViewModel.setCurrentTabId(position == 0 ? null : group.groupTabs.get(position - 1).id);
                        }
                    });


                    int defaultItem = 0;
                    if (defaultTabId != null) {
                        for (int i = 0; i < group.groupTabs.size(); i++) {
                            if (Objects.equals(group.groupTabs.get(i).id, defaultTabId)) {
                                defaultItem = i + 1;
                                break;
                            }
                        }
                    }
                    viewPager.setCurrentItem(defaultItem, false);
                }
            }
        });

    }


}