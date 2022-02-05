package com.doubean.ford.ui.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.repository.GroupRepository;

import java.util.List;

public class GroupDetailViewModel extends ViewModel {
    GroupRepository repository;

    private String groupId;

    private LiveData<Group> group;
    private LiveData<List<GroupTopic>> topics;

    public GroupDetailViewModel(GroupRepository repository, String groupId) {
        this.repository = repository;
        this.groupId = groupId;
        group = repository.getGroup(groupId);
        topics = repository.getGroupTopics(groupId);
    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<List<GroupTopic>> getTopics() {
        return topics;
    }
}