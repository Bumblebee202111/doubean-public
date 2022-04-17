package com.doubean.ford.ui.groups.groupDetail;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;
import com.doubean.ford.data.vo.GroupDetail;

public class GroupDetailViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private GroupFavoritesRepository groupFavoritesRepository;
    private String groupId;
    private MutableLiveData<String> currentTabId;
    private LiveData<Boolean> isCurrentTabFavorite;
    private final LiveData<GroupDetail> group;


    public GroupDetailViewModel(GroupRepository groupRepository, GroupFavoritesRepository groupFavoritesRepository, String groupId, String defaultTabId) {
        this.groupFavoritesRepository = groupFavoritesRepository;
        this.groupId = groupId;
        this.groupRepository = groupRepository;
        group = groupRepository.getGroup(groupId, true);
        this.currentTabId = new MutableLiveData<>(defaultTabId);
        this.isCurrentTabFavorite = Transformations.switchMap(currentTabId, new Function<String, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(String input) {
                return groupFavoritesRepository.getFavorite(groupId, input);
            }
        });


    }

    public void setCurrentTabId(String currentTabId) {
        this.currentTabId.setValue(currentTabId);
    }

    public LiveData<Boolean> getCurrentTabFavorite() {
        return isCurrentTabFavorite;
    }

    public LiveData<GroupDetail> getGroup() {
        return group;
    }

    public void addFavorite() {
        groupFavoritesRepository.createFavorite(groupId, currentTabId.getValue());
    }

    public void removeFavorite() {
        groupFavoritesRepository.removeFavorite(groupId, currentTabId.getValue());
    }
}