package com.doubean.ford.ui.groups.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;
import com.doubean.ford.data.vo.GroupDetail;
import com.doubean.ford.data.vo.Resource;

public class GroupDetailViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final LiveData<Resource<GroupDetail>> group;
    private final GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;
    private final String groupId;
    private final LiveData<Boolean> isGroupFollowed;

    public GroupDetailViewModel(GroupRepository groupRepository, GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository, String groupId, String defaultTabId) {
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.groupId = groupId;
        this.groupRepository = groupRepository;
        group = groupRepository.getGroup(groupId, true);
        this.isGroupFollowed = groupsFollowsAndSavesRepository.getFollowed(groupId, null);
    }

    public LiveData<Boolean> getFollowed() {
        return isGroupFollowed;
    }

    public LiveData<Resource<GroupDetail>> getGroup() {
        return group;
    }

    public void addFollow() {
        groupsFollowsAndSavesRepository.createFollow(groupId, null);
    }

    public void removeFollow() {
        groupsFollowsAndSavesRepository.removeFollow(groupId, null);
    }
}