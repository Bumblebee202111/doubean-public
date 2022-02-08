package com.doubean.ford.ui.groupTopicDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class GroupTopicDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String topicId;

    public GroupTopicDetailViewModelFactory(@NonNull GroupRepository repository, String topicId) {
        super();
        this.repository = repository;
        this.topicId = topicId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupTopicDetailViewModel(repository, topicId);
    }
}