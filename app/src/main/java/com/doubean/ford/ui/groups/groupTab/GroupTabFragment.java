package com.doubean.ford.ui.groups.groupTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.doubean.ford.adapters.GroupPostAdapter;
import com.doubean.ford.databinding.FragmentGroupTabBinding;
import com.doubean.ford.ui.groups.groupDetail.GroupDetailFragment;
import com.doubean.ford.util.InjectorUtils;

public class GroupTabFragment extends Fragment {
    public static final String ARG_GROUP_ID = "group_id";
    public static final String ARG_TAG_ID = "tag_id";
    FragmentGroupTabBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentGroupTabBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        Bundle args = requireArguments();
        String groupId = args.getString(ARG_GROUP_ID);
        String tagId = args.getString(ARG_TAG_ID);
        GroupTabViewModelFactory factory = InjectorUtils.provideGroupTabViewModelFactory(getContext(), groupId, tagId);
        GroupTabViewModel groupTabViewModel = new ViewModelProvider(this, factory).get(GroupTabViewModel.class);
        GroupPostAdapter adapter = new GroupPostAdapter();

        binding.postList.setAdapter(adapter);
        binding.postList.addItemDecoration(new DividerItemDecoration(binding.postList.getContext(), DividerItemDecoration.VERTICAL));
        groupTabViewModel.getPosts().observe(getViewLifecycleOwner(), adapter::submitList);
        GroupDetailFragment groupDetailFragment = (GroupDetailFragment) requireParentFragment();

        return binding.getRoot();
    }


}