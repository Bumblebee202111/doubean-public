package com.doubean.ford.ui.groups.groupsHome;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;

public class GroupsHomeViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;

    public GroupsHomeViewModelFactory(@NonNull GroupRepository repository, GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository) {
        super();
        this.repository = repository;
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupsHomeViewModel(repository, groupsFollowsAndSavesRepository);
    }
}
