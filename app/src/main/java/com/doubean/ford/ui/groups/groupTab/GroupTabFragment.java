package com.doubean.ford.ui.groups.groupTab;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.doubean.ford.R;
import com.doubean.ford.adapters.PostAdapter;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.GroupTab;
import com.doubean.ford.data.vo.PostSortBy;
import com.doubean.ford.databinding.FragmentGroupTabBinding;
import com.doubean.ford.util.InjectorUtils;
import com.doubean.ford.util.ShareUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class GroupTabFragment extends Fragment {
    public static final String ARG_GROUP_ID = "group_id";
    public static final String ARG_TAG_ID = "tag_id";
    private static final String ARG_GROUP = "group";
    FragmentGroupTabBinding binding;

    public static GroupTabFragment newInstance(String groupId, String tagId, GroupDetail groupDetail) {

        Bundle args = new Bundle();
        args.putString(GroupTabFragment.ARG_GROUP_ID, groupId);
        args.putString(GroupTabFragment.ARG_TAG_ID, tagId);
        args.putSerializable(GroupTabFragment.ARG_GROUP, groupDetail);
        GroupTabFragment fragment = new GroupTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentGroupTabBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        Bundle args = requireArguments();
        String groupId = args.getString(ARG_GROUP_ID);
        String tagId = args.getString(ARG_TAG_ID);
        GroupDetail group = (GroupDetail) args.getSerializable(ARG_GROUP);

        GroupTabViewModelFactory factory = InjectorUtils.provideGroupTabViewModelFactory(getContext(), groupId, tagId);
        GroupTabViewModel groupTabViewModel = new ViewModelProvider(this, factory).get(GroupTabViewModel.class);

        MaterialButton followUnfollow = binding.followUnfollow;

        binding.postList.addItemDecoration(new DividerItemDecoration(binding.postList.getContext(), DividerItemDecoration.VERTICAL));

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                groupTabViewModel.loadNextPage();
            }
        });


        PostAdapter adapter = new PostAdapter(group);
        binding.postList.setAdapter(adapter);
        groupTabViewModel.getPosts().observe(getViewLifecycleOwner(), result -> {
            binding.setFindResource(result);
            binding.setResultCount(result == null || result.data == null ? 0 : result.data.size());
            adapter.submitList(result == null ? null : result.data);
            if (result != null)
                binding.swiperefresh.setRefreshing(false);

        });

        int groupColor = group.getColor();
        followUnfollow.setIconTint(ColorStateList.valueOf(groupColor));
        followUnfollow.setTextColor(groupColor);

        binding.more.setOnClickListener(v -> {
            for (GroupTab tab : group.tabs) {
                if (tab.id.equals(tagId)) {
                    String shareText = group.name + "|" + tab.name + ' ' + group.url + "\r\n";
                    ShareUtil.Share(getContext(), shareText);
                    break;
                }
            }

        });

        groupTabViewModel.getLoadMoreStatus().observe(getViewLifecycleOwner(), loadingMore -> {
            if (loadingMore == null) {
                binding.setLoadingMore(false);
            } else {
                binding.setLoadingMore(loadingMore.isRunning());
                String error = loadingMore.getErrorMessageIfNotHandled();
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show();
                }
            }
            binding.executePendingBindings();
        });


        if (tagId == null) {//All
            binding.followUnfollow.setVisibility(View.GONE);
            binding.more.setVisibility(View.GONE);
        } else { //Non-all tab
            int colorSurface = getBackgroundColor();
            followUnfollow.setOnClickListener(v -> {
                Boolean followed = groupTabViewModel.getFollowed().getValue();
                if (followed != null) {
                    if (followed) {
                        groupTabViewModel.removeFollow();
                        Snackbar.make(binding.getRoot(), R.string.unfollowed_tab, Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        groupTabViewModel.addFollow();
                        Snackbar.make(binding.getRoot(), R.string.followed_tab, Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            });

            groupTabViewModel.getFollowed().observe(getViewLifecycleOwner(), followed -> {
                if (followed) {
                    followUnfollow.setIconResource(R.drawable.ic_remove);
                    followUnfollow.setText(R.string.unfollow);
                } else {
                    followUnfollow.setIconResource(R.drawable.ic_add);
                    followUnfollow.setText(R.string.follow);
                }
            });
        }

        Spinner spinner = binding.sortPostsBySpinner;
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_posts_by_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupTabViewModel.setSortBy(getSortByAt(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.setCallback(groupTabViewModel::refreshPosts);
        binding.swiperefresh.setOnRefreshListener(groupTabViewModel::refreshPosts);

        return binding.getRoot();
    }

    private int getBackgroundColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.backgroundColor, typedValue, true);
        return typedValue.data;
    }

    private PostSortBy getSortByAt(int position) {
        switch (position) {
            case 0:
                return PostSortBy.LAST_UPDATED;
            case 1:
                return PostSortBy.NEW;
            case 2:
                return PostSortBy.TOP;
        }
        return null;
    }
}