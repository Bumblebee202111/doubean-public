package com.doubean.ford.ui.groups.groupTab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.Group;
import com.doubean.ford.data.vo.GroupPost;

import java.util.List;

public class GroupTabViewModel extends ViewModel {
    GroupRepository repository;

    private String groupId;
    private String tagId;
    private LiveData<Group> group;
    private LiveData<List<GroupPost>> posts;

    public GroupTabViewModel(GroupRepository repository, String groupId, String tagId) {
        this.repository = repository;
        this.groupId = groupId;
        this.tagId = tagId;
        group = repository.getGroup(groupId, false);
        posts = repository.getGroupPosts(groupId, tagId);
    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<List<GroupPost>> getPosts() {
        return posts;
    }
}