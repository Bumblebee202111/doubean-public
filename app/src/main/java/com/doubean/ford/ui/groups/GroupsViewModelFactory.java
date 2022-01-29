package com.doubean.ford.ui.groups;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class GroupsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;

    public GroupsViewModelFactory(@NonNull GroupRepository repository) {
        super();
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupsViewModel(repository);
    }
}
