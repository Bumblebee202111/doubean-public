package com.doubean.ford.ui.groups.groupTab;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;

public class GroupTabViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String groupId;
    private String tagId;
    GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;

    public GroupTabViewModelFactory(@NonNull GroupRepository groupRepository, GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository, String groupId, String tagId) {
        super();
        this.repository = groupRepository;
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.groupId = groupId;
        this.tagId = tagId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        return (T) new GroupTabViewModel(repository, groupsFollowsAndSavesRepository, groupId, tagId);
    }
}