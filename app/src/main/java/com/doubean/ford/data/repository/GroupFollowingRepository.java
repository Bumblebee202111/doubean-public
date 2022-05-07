package com.doubean.ford.data.repository;

import androidx.lifecycle.LiveData;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupFollowedDao;
import com.doubean.ford.data.vo.GroupFollowed;
import com.doubean.ford.util.AppExecutors;

import java.util.List;

public class GroupFollowingRepository {
    private static GroupFollowingRepository instance;
    private final AppExecutors appExecutors;
    private final GroupFollowedDao groupFollowingDao;
    private final DoubanService doubanService;
    private final AppDatabase appDatabase;

    private GroupFollowingRepository(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        this.appExecutors = appExecutors;
        this.groupFollowingDao = appDatabase.getGroupFollowingDao();
        this.doubanService = doubanService;
        this.appDatabase = appDatabase;
    }

    public static GroupFollowingRepository getInstance(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        if (instance == null) {
            synchronized (GroupFollowingRepository.class) {
                if (instance == null) {
                    instance = new GroupFollowingRepository(appExecutors, appDatabase, doubanService);
                }
            }
        }
        return instance;
    }

    public LiveData<List<GroupFollowed>> getFollowedIds() {
        return groupFollowingDao.getFollowedIds();
    }

    public LiveData<Boolean> getFollowed(String groupId, String currentTabId) {
        return groupFollowingDao.getFollowed(groupId, currentTabId);
    }

    public void removeFollowed(String groupId, String currentTabId) {
        appExecutors.diskIO().execute(() -> groupFollowingDao.deleteFollowed(groupId, currentTabId));
    }

    public void createFollowed(String groupId, String groupTabId) {
        GroupFollowed groupFollowed = new GroupFollowed(groupId, groupTabId);
        appExecutors.diskIO().execute(() -> groupFollowingDao.insertFollowed(groupFollowed));
    }


}
