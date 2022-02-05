package com.doubean.ford.ui.groupDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class GroupDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String groupId;

    public GroupDetailViewModelFactory(@NonNull GroupRepository repository, String groupId) {
        super();
        this.repository = repository;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupDetailViewModel(repository, groupId);
    }
}