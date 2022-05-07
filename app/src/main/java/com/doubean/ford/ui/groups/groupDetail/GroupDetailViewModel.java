package com.doubean.ford.ui.groups.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupFollowingRepository;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.GroupDetail;

public class GroupDetailViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final LiveData<GroupDetail> group;
    private final GroupFollowingRepository groupFollowingRepository;
    private final String groupId;
    private final MutableLiveData<String> currentTabId;
    private final LiveData<Boolean> isCurrentTabFollowed;


    public GroupDetailViewModel(GroupRepository groupRepository, GroupFollowingRepository groupFollowingRepository, String groupId, String defaultTabId) {
        this.groupFollowingRepository = groupFollowingRepository;
        this.groupId = groupId;
        this.groupRepository = groupRepository;
        group = groupRepository.getGroup(groupId, true);
        this.currentTabId = new MutableLiveData<>(defaultTabId);
        this.isCurrentTabFollowed = Transformations.switchMap(currentTabId,
                input -> groupFollowingRepository.getFollowed(groupId, input));


    }

    public void setCurrentTabId(String currentTabId) {
        this.currentTabId.setValue(currentTabId);
    }

    public LiveData<Boolean> getCurrentTabFollowed() {
        return isCurrentTabFollowed;
    }

    public LiveData<GroupDetail> getGroup() {
        return group;
    }

    public void addFollowed() {
        groupFollowingRepository.createFollowed(groupId, currentTabId.getValue());
    }

    public void removeFollowed() {
        groupFollowingRepository.removeFollowed(groupId, currentTabId.getValue());
    }
}