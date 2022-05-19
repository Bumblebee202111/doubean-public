package com.doubean.ford.ui.groups.groupTab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowedAndSavedRepository;
import com.doubean.ford.data.vo.GroupPostItem;

import java.util.List;

public class GroupTabViewModel extends ViewModel {
    GroupRepository repository;

    private final LiveData<Boolean> followed;
    private String groupId;
    private String tagId;
    private LiveData<List<GroupPostItem>> posts;
    private GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository;


    public GroupTabViewModel(GroupRepository repository, GroupsFollowedAndSavedRepository groupsFollowedAndSavedRepository, String groupId, String tagId) {
        this.repository = repository;
        this.groupsFollowedAndSavedRepository = groupsFollowedAndSavedRepository;
        this.groupId = groupId;
        this.tagId = tagId;
        posts = repository.getGroupPosts(groupId, tagId);
        this.followed = groupsFollowedAndSavedRepository.getFollowed(groupId, tagId);

    }


    public LiveData<List<GroupPostItem>> getPosts() {
        return posts;
    }

    public LiveData<Boolean> getFollowed() {
        return followed;
    }

    public void addFollowed() {
        groupsFollowedAndSavedRepository.createFollowed(groupId, tagId);
    }

    public void removeFollowed() {
        groupsFollowedAndSavedRepository.removeFollowed(groupId, tagId);
    }
}