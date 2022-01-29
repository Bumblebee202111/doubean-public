package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.doubean.ford.data.Group;

import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getGroups();

    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    LiveData<Group> getGroup(int groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Group> groupList);
}
