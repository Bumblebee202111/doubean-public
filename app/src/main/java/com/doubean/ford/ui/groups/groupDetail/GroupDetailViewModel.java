package com.doubean.ford.ui.groups.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowedAndSavedRepository;
import com.doubean.ford.data.vo.GroupDetail;

public class GroupDetailViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final LiveData<GroupDetail> group;
    private final GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository;
    private final String groupId;
    private final LiveData<Boolean> isGroupFollowed;
    private final MutableLiveData<Integer> currentItem = new MutableLiveData<>();

    public GroupDetailViewModel(GroupRepository groupRepository, GroupsFollowedAndSavedRepository followedAndSavedRepository, String groupId, String defaultTabId) {
        this.groupsFollowedAndSavedRepository = followedAndSavedRepository;
        this.groupId = groupId;
        this.groupRepository = groupRepository;
        group = groupRepository.getGroup(groupId, true);
        this.isGroupFollowed = followedAndSavedRepository.getFollowed(groupId, null);

    }

    public MutableLiveData<Integer> getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int position) {
        this.currentItem.setValue(position);
    }

    public LiveData<Boolean> getFollowed() {
        return isGroupFollowed;
    }

    public LiveData<GroupDetail> getGroup() {
        return group;
    }

    public void addFollowed() {
        groupsFollowedAndSavedRepository.createFollowed(groupId, null);
    }

    public void removeFollowed() {
        groupsFollowedAndSavedRepository.removeFollowed(groupId, null);
    }
}