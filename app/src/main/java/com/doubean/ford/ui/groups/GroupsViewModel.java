package com.doubean.ford.ui.groups;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupFavorite;
import com.doubean.ford.data.GroupFavoriteDetail;
import com.doubean.ford.data.repository.GroupFavoritesRepository;
import com.doubean.ford.data.repository.GroupRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupsViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final GroupFavoritesRepository groupFavoritesRepository;
    private final LiveData<List<GroupFavoriteDetail>> favorites;


    public GroupsViewModel(@NonNull GroupRepository groupRepository, @NonNull GroupFavoritesRepository groupFavoritesRepository) {
        super();
        this.groupFavoritesRepository = groupFavoritesRepository;
        this.favorites = createFavoritesLiveData();
        this.groupRepository = groupRepository;

    }

    public LiveData<List<GroupFavoriteDetail>> getFavorites() {
        return favorites;
    }

    private LiveData<List<GroupFavoriteDetail>> createFavoritesLiveData() {
        MediatorLiveData<List<GroupFavoriteDetail>> favoritesLiveData = new MediatorLiveData<>();
        favoritesLiveData.addSource(groupFavoritesRepository.getFavoriteIds(), new Observer<List<GroupFavorite>>() {
            @Override
            public void onChanged(List<GroupFavorite> favoriteIds) {
                if (favoriteIds != null) {
                    List<GroupFavoriteDetail> favorites = new ArrayList<>(Arrays.asList(new GroupFavoriteDetail[favoriteIds.size()]));
                    for (int i = 0; i < favoriteIds.size(); i++) {
                        LiveData<Group> groupLiveData = groupRepository.getGroup(favoriteIds.get(i).groupId, false);
                        int finalI = i;
                        favoritesLiveData.addSource(groupLiveData, new Observer<Group>() {
                            @Override
                            public void onChanged(Group group) {
                                if (group != null) {
                                    GroupFavoriteDetail groupFavoriteDetail = new GroupFavoriteDetail(group, favoriteIds.get(finalI).groupTabId);
                                    favorites.set(finalI, groupFavoriteDetail);
                                    favoritesLiveData.setValue(favorites);
                                    favoritesLiveData.removeSource(groupLiveData);
                                }

                            }
                        });
                    }
                }
            }
        });
        return favoritesLiveData;
    }
}