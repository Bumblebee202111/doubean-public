package com.doubean.ford.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.doubean.ford.R;
import com.doubean.ford.adapters.GroupAdapter;
import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.databinding.FragmentGroupsBinding;
import com.doubean.ford.util.AppExecutors;
import com.doubean.ford.util.SpanCountCalculator;

import java.util.List;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;
    private GroupsViewModelFactory factory;
    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        factory = new GroupsViewModelFactory(GroupRepository.getInstance(new AppExecutors()
                , AppDatabase.getInstance(requireContext().getApplicationContext())
                , DoubanService.create()));
        groupsViewModel =
                new ViewModelProvider(this, factory).get(GroupsViewModel.class);
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        GroupAdapter adapter = new GroupAdapter();
        binding.groupList.setAdapter(adapter);

        groupsViewModel.getFavGroups().observe(getViewLifecycleOwner(), new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                if (groups != null) {
                    adapter.submitList(groups);
                }
            }
        });
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
        binding = null;
    }
}