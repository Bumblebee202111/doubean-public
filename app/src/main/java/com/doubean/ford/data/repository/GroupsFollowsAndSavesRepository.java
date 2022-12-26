package com.doubean.ford.data.repository;

import androidx.lifecycle.LiveData;

import com.doubean.ford.api.DoubanService;
import com.doubean.ford.data.db.AppDatabase;
import com.doubean.ford.data.db.GroupFollowsAndSavesDao;
import com.doubean.ford.data.vo.GroupFollow;
import com.doubean.ford.util.AppExecutors;

import java.util.List;

public class GroupsFollowsAndSavesRepository {
    private static GroupsFollowsAndSavesRepository instance;
    private final AppExecutors appExecutors;
    private final GroupFollowsAndSavesDao groupFollowsAndSavesDao;
    private final DoubanService doubanService;
    private final AppDatabase appDatabase;

    private GroupsFollowsAndSavesRepository(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        this.appExecutors = appExecutors;
        this.groupFollowsAndSavesDao = appDatabase.getGroupFollowsAndSavesDao();
        this.doubanService = doubanService;
        this.appDatabase = appDatabase;
    }

    public static GroupsFollowsAndSavesRepository getInstance(AppExecutors appExecutors, AppDatabase appDatabase, DoubanService doubanService) {
        if (instance == null) {
            synchronized (GroupsFollowsAndSavesRepository.class) {
                if (instance == null) {
                    instance = new GroupsFollowsAndSavesRepository(appExecutors, appDatabase, doubanService);
                }
            }
        }
        return instance;
    }

    public LiveData<List<GroupFollow>> getFollowIds() {
        return groupFollowsAndSavesDao.loadFollowIds();
    }

    public LiveData<Boolean> getFollowed(String groupId, String tabId) {
        return groupFollowsAndSavesDao.loadFollowed(groupId, tabId);
    }

    public void removeFollow(String groupId, String tabId) {
        appExecutors.diskIO().execute(() -> groupFollowsAndSavesDao.deleteFollow(groupId, tabId));
    }

    public void createFollow(String groupId, String groupTabId) {
        GroupFollow groupFollow = new GroupFollow(groupId, groupTabId);
        appExecutors.diskIO().execute(() -> groupFollowsAndSavesDao.insertFollow(groupFollow));
    }


}
