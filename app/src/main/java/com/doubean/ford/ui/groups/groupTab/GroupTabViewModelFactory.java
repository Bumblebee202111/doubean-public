package com.doubean.ford.ui.groups.groupTab;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class GroupTabViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private String groupId;
    private String tagId;

    public GroupTabViewModelFactory(@NonNull GroupRepository repository, String groupId, String tagId) {
        super();
        this.repository = repository;
        this.groupId = groupId;
        this.tagId = tagId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupTabViewModel(repository, groupId, tagId);
    }
}