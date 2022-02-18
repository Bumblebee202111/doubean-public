package com.doubean.ford.ui.groupTopic;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class GroupTopicViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String topicId;

    public GroupTopicViewModelFactory(@NonNull GroupRepository repository, String topicId) {
        super();
        this.repository = repository;
        this.topicId = topicId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupTopicViewModel(repository, topicId);
    }
}