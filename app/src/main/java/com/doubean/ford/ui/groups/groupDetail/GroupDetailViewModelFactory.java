package com.doubean.ford.ui.groups.groupDetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.doubean.ford.data.repository.GroupFollowingRepository;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private GroupRepository repository;
    private GroupFollowingRepository groupFollowingRepository;
    private String groupId;
    private String defaultTab;

    public GroupDetailViewModelFactory(@NonNull GroupRepository repository, @NonNull GroupFollowingRepository groupFollowingRepository, String groupId, String defaultTab) {
        super();
        this.repository = repository;
        this.groupFollowingRepository = groupFollowingRepository;
        this.groupId = groupId;
        this.defaultTab = defaultTab;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupDetailViewModel(repository, groupFollowingRepository, groupId, defaultTab);
    }
}