package com.doubean.ford.ui.groupTopicDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupTopicDetailViewModel extends ViewModel {
    private GroupRepository repository;
    private LiveData<GroupTopic> groupTopic;

    public GroupTopicDetailViewModel(GroupRepository repository, String topicId) {
        this.repository = repository;
        groupTopic = repository.getGroupTopic(topicId);
    }

    public LiveData<GroupTopic> getGroupTopic() {
        return groupTopic;
    }
}