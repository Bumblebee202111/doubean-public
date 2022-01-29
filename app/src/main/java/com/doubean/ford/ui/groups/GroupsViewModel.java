package com.doubean.ford.ui.groups;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.repository.GroupRepository;

import java.util.List;

public class GroupsViewModel extends ViewModel {
    LiveData<List<Group>> groups;
    private GroupRepository groupRepository;

    public GroupsViewModel(@NonNull GroupRepository groupRepository) {
        super();
        this.groupRepository = groupRepository;
        this.groupRepository = groupRepository;
        this.groups = this.groupRepository.getGroups();
    }

    public LiveData<List<Group>> getGroups() {
        return groups;
    }
}