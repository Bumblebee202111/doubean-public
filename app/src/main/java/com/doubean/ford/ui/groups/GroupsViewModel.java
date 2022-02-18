package com.doubean.ford.ui.groups;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
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
                                    boolean loaded = true;
                                    for (GroupFavoriteDetail favorite : favorites) {
                                        if (favorite == null) {
                                            loaded = false;
                                            break;
                                        }
                                    }
                                    if (loaded)
                                        favoritesLiveData.postValue(favorites);
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

    private LiveData<List<GroupFavoriteDetail>> createFavoritesLiveData1() {
        MediatorLiveData<List<GroupFavoriteDetail>> favoritesLiveData = new MediatorLiveData<>();

        favoritesLiveData.addSource(groupFavoritesRepository.getFavoriteIds(), favoriteIds -> {
            if (favoriteIds != null) {
                List<LiveData<GroupFavoriteDetail>> favoriteLiveDataList = new ArrayList<>();
                for (int i = 0; i < favoriteIds.size(); i++)
                    favoriteLiveDataList.add(new MutableLiveData<>());

                for (int i = 1; i < favoriteLiveDataList.size(); i++) {
                    int index = i;
                    favoriteLiveDataList.set(i, Transformations.switchMap(favoriteLiveDataList.get(i - 1), new Function<GroupFavoriteDetail, LiveData<GroupFavoriteDetail>>() {
                        @Override
                        public LiveData<GroupFavoriteDetail> apply(GroupFavoriteDetail favorite) {
                            LiveData<GroupFavoriteDetail> favoriteDetail = null;
                            if (favorite != null) {
                                favoriteDetail = getGroupFavorite(favoriteIds.get(index));
                            }
                            return favoriteDetail;
                        }
                    }));
                }
                favoriteLiveDataList.set(0, getGroupFavorite(favoriteIds.get(0)));
                favoritesLiveData.addSource(favoriteLiveDataList.get(favoriteLiveDataList.size() - 1), new Observer<GroupFavoriteDetail>() {
                    @Override
                    public void onChanged(GroupFavoriteDetail groupFavoriteDetail) {
                        if (groupFavoriteDetail != null) {
                            List<GroupFavoriteDetail> favoriteDetailList = new ArrayList<>();
                            for (LiveData<GroupFavoriteDetail> favoriteDetail : favoriteLiveDataList) {
                                favoriteDetailList.add(favoriteDetail.getValue());
                            }
                            favoritesLiveData.setValue(favoriteDetailList);
                        }

                    }
                });
            }


        });

        return favoritesLiveData;
    }

    private LiveData<GroupFavoriteDetail> getGroupFavorite(GroupFavorite favorite) {
        LiveData<Group> groupLiveData = groupRepository.getGroup(favorite.groupId, false);
        return Transformations.map(groupLiveData,
                new Function<Group, GroupFavoriteDetail>() {
                    @Override
                    public GroupFavoriteDetail apply(Group group) {
                        return new GroupFavoriteDetail(group, favorite.groupTabId);
                    }
                });
    }

}