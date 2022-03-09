package com.doubean.ford.ui.groups;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.doubean.ford.R;
import com.doubean.ford.adapters.GroupFavoriteAdapter;
import com.doubean.ford.databinding.FragmentGroupsBinding;
import com.doubean.ford.util.InjectorUtils;
import com.doubean.ford.util.SpanCountCalculator;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;
    private GroupsViewModelFactory factory;
    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        factory = InjectorUtils.provideGroupsViewModelFactory(requireContext());
        groupsViewModel =
                new ViewModelProvider(this, factory).get(GroupsViewModel.class);
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.groupList.addItemDecoration(new DividerItemDecoration(binding.groupList.getContext(), DividerItemDecoration.VERTICAL));
        GroupFavoriteAdapter adapter = new GroupFavoriteAdapter();
        binding.groupList.setAdapter(adapter);
        groupsViewModel.getFavorites().observe(getViewLifecycleOwner(), adapter::submitList);
        int spanCount = SpanCountCalculator.getSpanCount(requireContext(), 400);
        binding.groupList.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.inflateMenu(R.menu.groups_menu);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_search:
                    NavDirections direction = GroupsFragmentDirections.actionNavigationGroupsToNavigationGroupSearch();
                    Navigation.findNavController(view).navigate(direction);
                    return true;
                case R.id.action_settings:

                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}