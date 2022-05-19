package com.doubean.ford.ui.groups.groupsHome;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowedAndSavedRepository;

public class GroupsHomeViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository;

    public GroupsHomeViewModelFactory(@NonNull GroupRepository repository, GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository) {
        super();
        this.repository = repository;
        this.groupsFollowedAndSavedRepository = groupsFollowedAndSavedRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupsHomeViewModel(repository, groupsFollowedAndSavedRepository);
    }
}
