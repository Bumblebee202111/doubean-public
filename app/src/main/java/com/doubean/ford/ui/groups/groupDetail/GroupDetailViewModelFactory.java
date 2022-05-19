package com.doubean.ford.ui.groups.groupDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowedAndSavedRepository;

public class GroupDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository;
    private String groupId;
    private String defaultTab;

    public GroupDetailViewModelFactory(@NonNull GroupRepository repository, @NonNull GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository, String groupId, String defaultTab) {
        super();
        this.repository = repository;
        this.groupsFollowedAndSavedRepository = groupsFollowedAndSavedRepository;
        this.groupId = groupId;
        this.defaultTab = defaultTab;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupDetailViewModel(repository, groupsFollowedAndSavedRepository, groupId, defaultTab);
    }
}