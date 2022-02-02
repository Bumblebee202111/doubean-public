package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.doubean.ford.data.FavGroup;
import com.doubean.ford.data.Group;
import com.doubean.ford.data.GroupSearchResult;

import java.util.Comparator;
import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getGroups();

    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    LiveData<Group> getGroup(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(Group... groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(List<Group> groupList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavGroup(FavGroup favGroup);

    @Transaction
    @Query("SELECT * FROM groups WHERE groupId IN (SELECT groupId FROM fav_groups)")
    LiveData<List<Group>> getAllFavGroups();

    @Query("SELECT * FROM fav_groups")
    LiveData<List<FavGroup>> getAllFavGroupIds();

    @Query("SELECT * FROM GroupSearchResult WHERE `query` = :query")
    LiveData<GroupSearchResult> search(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GroupSearchResult repoSearchResult);

    default LiveData<List<Group>> loadOrdered(List<String> groupIds, Comparator<Group> c) {

        return Transformations.map(loadById(groupIds), repositories -> {
            repositories.sort(c);
            return repositories;
        });

    }

    @Query("SELECT * FROM groups WHERE groupId in (:groupIds)")
    LiveData<List<Group>> loadById(List<String> groupIds);
}
