package com.doubean.ford.ui.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupDetailViewModel extends ViewModel {
    String groupId;
    GroupRepository repository;
    private final LiveData<Group> group;
    private LiveData<Boolean> isFavorite;
    public GroupDetailViewModel(GroupRepository repository, String groupId) {
        this.groupId = groupId;
        this.repository = repository;
        group = repository.getGroup(groupId);
        isFavorite = repository.isFavorite(groupId);

    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void addGroupToFavorites() {
        repository.createFavoriteGroup(groupId);
    }

    public void removeGroupFromFavorites() {
        repository.removeFavoriteGroup(groupId);
    }
}