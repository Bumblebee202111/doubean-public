package com.doubean.ford.ui.groupDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.adapters.GroupTopicAdapter;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.databinding.GroupDetailFragmentBinding;
import com.doubean.ford.util.InjectorUtils;

import java.util.List;

//TODO: refactor it to general Fragment for custom group/tag subscription
public class GroupDetailFragment extends Fragment {

    private GroupDetailViewModel groupDetailViewModel;

    public static GroupDetailFragment newInstance() {
        return new GroupDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        GroupDetailFragmentBinding binding = GroupDetailFragmentBinding.inflate(inflater, container, false);
        GroupDetailFragmentArgs args = GroupDetailFragmentArgs.fromBundle(requireArguments());
        GroupDetailViewModelFactory factory = InjectorUtils.provideGroupDetailViewModelFactory(requireContext(), args.getGroupId());
        groupDetailViewModel = new ViewModelProvider(this, factory).get(GroupDetailViewModel.class);
        binding = GroupDetailFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupTopicAdapter adapter = new GroupTopicAdapter();
        binding.topicList.setAdapter(adapter);
        //groupDetailViewModel.getGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
        //   @Override
        //    public void onChanged(@NonNull Group group) {
        //        binding.setGroup(group);
        //    }
        // });
        groupDetailViewModel.getTopics().observe(getViewLifecycleOwner(), new Observer<List<GroupTopic>>() {
            @Override
            public void onChanged(@NonNull List<GroupTopic> groupTopics) {
                adapter.submitList(groupTopics);
            }
        });
        return binding.getRoot();
    }


}