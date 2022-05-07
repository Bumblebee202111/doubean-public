package com.doubean.ford.ui.groups.groupsHome;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupFollowingRepository;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupsHomeViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupFollowingRepository groupFollowingRepository;

    public GroupsHomeViewModelFactory(@NonNull GroupRepository repository, GroupFollowingRepository groupFollowingRepository) {
        super();
        this.repository = repository;
        this.groupFollowingRepository = groupFollowingRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupsHomeViewModel(repository, groupFollowingRepository);
    }
}
