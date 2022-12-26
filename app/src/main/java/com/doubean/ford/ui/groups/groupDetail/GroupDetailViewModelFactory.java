package com.doubean.ford.ui.groups.groupDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;

public class GroupDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;
    private String groupId;
    private String defaultTab;

    public GroupDetailViewModelFactory(@NonNull GroupRepository repository, @NonNull GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository, String groupId, String defaultTab) {
        super();
        this.repository = repository;
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.groupId = groupId;
        this.defaultTab = defaultTab;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupDetailViewModel(repository, groupsFollowsAndSavesRepository, groupId, defaultTab);
    }
}