package com.doubean.ford.ui.groups.groupDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupFavoritesRepository groupFavoritesRepository;
    private String groupId;
    private String defaultTab;

    public GroupDetailViewModelFactory(@NonNull GroupRepository repository, @NonNull GroupFavoritesRepository groupFavoritesRepository, String groupId, String defaultTab) {
        super();
        this.repository = repository;
        this.groupFavoritesRepository = groupFavoritesRepository;
        this.groupId = groupId;
        this.defaultTab = defaultTab;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupDetailViewModel(repository, groupFavoritesRepository, groupId, defaultTab);
    }
}