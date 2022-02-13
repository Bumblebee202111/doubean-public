package com.doubean.ford.ui.groupDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;

public class GroupDetailViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private GroupFavoritesRepository groupFavoritesRepository;
    private String groupId;
    private MutableLiveData<String> currentTabId;
    private LiveData<Boolean> isCurrentTabFavorite;
    private final LiveData<Group> group;


    public GroupDetailViewModel(GroupRepository groupRepository, GroupFavoritesRepository groupFavoritesRepository, String groupId, String defaultTabId) {
        this.groupFavoritesRepository = groupFavoritesRepository;
        this.groupId = groupId;
        this.groupRepository = groupRepository;
        this.currentTabId = new MutableLiveData<>(defaultTabId);
        group = groupRepository.getGroup(groupId, true);
        this.isCurrentTabFavorite = Transformations.switchMap(currentTabId, input -> groupFavoritesRepository.getFavorite(groupId, input));


    }

    public void setCurrentTabId(String currentTabId) {
        this.currentTabId.setValue(currentTabId);
    }

    public LiveData<Boolean> getCurrentTabFavorite() {
        return isCurrentTabFavorite;
    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public void addFavorite() {
        groupFavoritesRepository.createFavorite(groupId, currentTabId.getValue());
    }

    public void removeFavorite() {
        groupFavoritesRepository.removeFavorite(groupId, currentTabId.getValue());
    }
}