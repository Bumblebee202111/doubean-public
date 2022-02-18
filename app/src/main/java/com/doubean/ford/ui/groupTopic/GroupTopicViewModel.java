package com.doubean.ford.ui.groupTopic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicComments;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupTopicViewModel extends ViewModel {
    String topicId;
    private GroupRepository groupRepository;
    private LiveData<GroupTopic> groupTopic;
    private LiveData<GroupTopicComments> groupTopicComments;

    public GroupTopicViewModel(GroupRepository groupRepository, String topicId) {
        this.groupRepository = groupRepository;
        this.topicId = topicId;
        this.groupTopic = groupRepository.getGroupTopic(topicId);
        this.groupTopicComments = groupRepository.getGroupTopicComments(topicId);
    }

    public LiveData<GroupTopic> getTopic() {
        return groupTopic;
    }

    public LiveData<GroupTopicComments> getTopicComments() {
        return groupTopicComments;
    }
}