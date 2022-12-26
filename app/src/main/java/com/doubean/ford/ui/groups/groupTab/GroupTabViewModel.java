package com.doubean.ford.ui.groups.groupTab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.repository.GroupsFollowsAndSavesRepository;
import com.doubean.ford.data.vo.GroupPostItem;
import com.doubean.ford.data.vo.Resource;

import java.util.List;

public class GroupTabViewModel extends ViewModel {
    GroupRepository repository;

    private final LiveData<Boolean> followed;
    private String groupId;
    private String tagId;
    private LiveData<Resource<List<GroupPostItem>>> posts;
    private GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository;


    public GroupTabViewModel(GroupRepository repository, GroupsFollowsAndSavesRepository groupsFollowsAndSavesRepository, String groupId, String tagId) {
        this.repository = repository;
        this.groupsFollowsAndSavesRepository = groupsFollowsAndSavesRepository;
        this.groupId = groupId;
        this.tagId = tagId;
        posts = repository.getGroupPosts(groupId, tagId);
        this.followed = groupsFollowsAndSavesRepository.getFollowed(groupId, tagId);

    }


    public LiveData<Resource<List<GroupPostItem>>> getPosts() {
        return posts;
    }

    public LiveData<Boolean> getFollowed() {
        return followed;
    }

    public void addFollow() {
        groupsFollowsAndSavesRepository.createFollow(groupId, tagId);
    }

    public void removeFollow() {
        groupsFollowsAndSavesRepository.removeFollow(groupId, tagId);
    }
}