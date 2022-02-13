package com.doubean.ford.ui.groups;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupFavoritesRepository groupFavoritesRepository;

    public GroupsViewModelFactory(@NonNull GroupRepository repository, GroupFavoritesRepository groupFavoritesRepository) {
        super();
        this.repository = repository;
        this.groupFavoritesRepository = groupFavoritesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupsViewModel(repository, groupFavoritesRepository);
    }
}
