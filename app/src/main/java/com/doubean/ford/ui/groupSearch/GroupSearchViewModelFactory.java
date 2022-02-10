package com.doubean.ford.ui.groupSearch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupRepository;

public class GroupSearchViewModelFactory implements ViewModelProvider.Factory {
    private GroupRepository groupRepository;

    public GroupSearchViewModelFactory(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupSearchViewModel(groupRepository);
    }
}
