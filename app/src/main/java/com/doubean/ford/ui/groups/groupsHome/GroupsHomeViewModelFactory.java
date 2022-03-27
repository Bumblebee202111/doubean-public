package com.doubean.ford.ui.groups.groupsHome;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupsHomeViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupFavoritesRepository groupFavoritesRepository;

    public GroupsHomeViewModelFactory(@NonNull GroupRepository repository, GroupFavoritesRepository groupFavoritesRepository) {
        super();
        this.repository = repository;
        this.groupFavoritesRepository = groupFavoritesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupsHomeViewModel(repository, groupFavoritesRepository);
    }
}
