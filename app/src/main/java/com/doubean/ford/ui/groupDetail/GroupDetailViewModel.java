package com.doubean.ford.ui.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupDetailViewModel extends ViewModel {
    GroupRepository repository;
    private final LiveData<Group> group;
    public GroupDetailViewModel(GroupRepository repository, String groupId) {
        this.repository = repository;

        group = repository.getGroup(groupId);
    }

    public LiveData<Group> getGroup() {
        return group;
    }

}