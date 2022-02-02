package com.doubean.ford.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.adapters.GroupAdapter;
import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.databinding.FragmentGroupsBinding;
import com.doubean.ford.util.AppExecutors;

import java.util.List;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;
    private GroupsViewModelFactory factory;
    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        factory = new GroupsViewModelFactory(GroupRepository.getInstance(new AppExecutors()
                , AppDatabase.getInstance(getContext().getApplicationContext())
                , DoubanService.create()));
        groupsViewModel =
                new ViewModelProvider(this, factory).get(GroupsViewModel.class);
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        GroupAdapter adapter = new GroupAdapter();
        binding.groupList.setAdapter(adapter);
        //groupsViewModel.addFavGroup(new FavGroup(644960));
        groupsViewModel.getFavGroups().observe(getViewLifecycleOwner(), new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                if (groups != null) {
                    adapter.submitList(groups);
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}