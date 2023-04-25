package com.doubean.ford.ui.groups.groupsHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.doubean.ford.R;
import com.doubean.ford.adapters.GroupFollowAdapter;
import com.doubean.ford.adapters.RecommendedGroupAdapter;
import com.doubean.ford.databinding.FragmentGroupsBinding;
import com.doubean.ford.util.InjectorUtils;

import java.util.ArrayList;

public class GroupsHomeFragment extends Fragment {

    private GroupsHomeViewModel groupsHomeViewModel;
    private GroupsHomeViewModelFactory factory;
    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        factory = InjectorUtils.provideGroupsViewModelFactory(requireContext());
        groupsHomeViewModel =
                new ViewModelProvider(this, factory).get(GroupsHomeViewModel.class);
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        binding.setViewModel(groupsHomeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        GroupFollowAdapter adapter = new GroupFollowAdapter();
        binding.followList.setAdapter(adapter);
        groupsHomeViewModel.getFollowList().observe(getViewLifecycleOwner(), list -> adapter.submitList(list == null ? null : new ArrayList<>(list)));

        RecommendedGroupAdapter recommendedGroupAdapter = new RecommendedGroupAdapter(getContext());
        binding.groupsOfTheDayList.setAdapter(recommendedGroupAdapter);
        groupsHomeViewModel.getGroupsOfTheDay().observe(getViewLifecycleOwner(), groups -> recommendedGroupAdapter.submitList(groups == null || groups.data == null ? null : new ArrayList<>(groups.data)));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.inflateMenu(R.menu.groups_menu);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_search: {
                    NavDirections action = GroupsHomeFragmentDirections.actionNavigationGroupsToNavigationGroupSearch();
                    Navigation.findNavController(view).navigate(action);
                    return true;
                }
                case R.id.action_settings:
                    Navigation.findNavController(view).navigate(R.id.navigation_settings_fragment);
                    return true;
                default:
                    return false;
            }
        });
    }

}