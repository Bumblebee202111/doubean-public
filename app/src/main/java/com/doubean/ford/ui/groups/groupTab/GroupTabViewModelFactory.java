package com.doubean.ford.ui.groups.groupTab;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowedAndSavedRepository;

public class GroupTabViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String groupId;
    private String tagId;
    GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository;

    public GroupTabViewModelFactory(@NonNull GroupRepository groupRepository, GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository, String groupId, String tagId) {
        super();
        this.repository = groupRepository;
        this.groupsFollowedAndSavedRepository = groupsFollowedAndSavedRepository;
        this.groupId = groupId;
        this.tagId = tagId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        return (T) new GroupTabViewModel(repository, groupsFollowedAndSavedRepository, groupId, tagId);
    }
}