package com.doubean.ford.data.repository;

import androidx.lifecycle.LiveData;

import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupRemoteDataSource;
import com.doubean.ford.data.db.GroupDao;

import java.util.List;

public class GroupRepository {

    private static GroupRepository instance;
    private GroupDao groupDao;
    private GroupRemoteDataSource groupRemoteDataSource;

    private GroupRepository(GroupDao gardenPlantingDao) {
        this.groupDao = gardenPlantingDao;
    }

    public static GroupRepository getInstance(GroupDao gardenPlantingDao) {
        if (instance == null) {
            synchronized (GroupRepository.class) {
                if (instance == null) {
                    instance = new GroupRepository(gardenPlantingDao);
                }
            }
        }
        return instance;
    }

    public LiveData<List<Group>> getGroups() {
        return this.groupDao.getGroups();
    }

    public LiveData<Group> getGroup(int groupId) {
        return this.groupDao.getGroup(groupId);
        //return this.groupRemoteDataSource.getGroup(groupId);
    }
}
