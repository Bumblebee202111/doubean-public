package com.doubean.ford.ui.groupTab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.repository.GroupRepository;

import java.util.List;

public class GroupTabViewModel extends ViewModel {
    GroupRepository repository;

    private String groupId;
    private String tagId;
    private LiveData<Group> group;
    private LiveData<List<GroupTopic>> topics;

    public GroupTabViewModel(GroupRepository repository, String groupId, String tagId) {
        this.repository = repository;
        this.groupId = groupId;
        this.tagId = tagId;
        group = repository.getGroup(groupId);
        topics = repository.getGroupTopics(groupId, tagId);
    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<List<GroupTopic>> getTopics() {
        return topics;
    }
}