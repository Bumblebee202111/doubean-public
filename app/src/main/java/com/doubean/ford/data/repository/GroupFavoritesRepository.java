package com.doubean.ford.data.repository;

import androidx.lifecycle.LiveData;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.GroupFavorite;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupFavoritesDao;
import com.doubean.ford.util.AppExecutors;

import java.util.List;

public class GroupFavoritesRepository {
    private static GroupFavoritesRepository instance;
    private final AppExecutors appExecutors;
    private final GroupFavoritesDao groupFavoritesDao;
    private final DoubanService doubanService;
    private final AppDatabase appDatabase;

    private GroupFavoritesRepository(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        this.appExecutors = appExecutors;
        this.groupFavoritesDao = appDatabase.getFavoriteGroupDao();
        this.doubanService = doubanService;
        this.appDatabase = appDatabase;
    }

    public static GroupFavoritesRepository getInstance(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        if (instance == null) {
            synchronized (GroupFavoritesRepository.class) {
                if (instance == null) {
                    instance = new GroupFavoritesRepository(appExecutors, appDatabase, doubanService);
                }
            }
        }
        return instance;
    }

    public LiveData<List<GroupFavorite>> getFavoriteIds() {
        return groupFavoritesDao.getFavoriteGroupAndTabIds();
    }

    public LiveData<Boolean> getFavorite(String groupId, String currentTabId) {
        return groupFavoritesDao.getFavorite(groupId, currentTabId);
    }

    public void removeFavorite(String groupId, String currentTabId) {
        appExecutors.diskIO().execute(() -> groupFavoritesDao.deleteGroupFavorite(groupId, currentTabId));
    }

    public void createFavorite(String groupId, String groupTabId) {
        GroupFavorite groupFavorite = new GroupFavorite(groupId, groupTabId);
        appExecutors.diskIO().execute(() -> groupFavoritesDao.insertGroupFavorite(groupFavorite));
    }


}
