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
import com.doubean.ford.data.GroupTopic;

import java.util.Comparator;
import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getGroups();

    @Query("SELECT * FROM groups WHERE id = :groupId")
    LiveData<Group> getGroup(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(Group... groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(List<Group> groupList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavGroup(FavGroup favGroup);

    @Transaction
    @Query("SELECT * FROM groups WHERE id IN (SELECT id FROM fav_groups)")
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

    @Query("SELECT * FROM groups WHERE id in (:groupIds)")
    LiveData<List<Group>> loadById(List<String> groupIds);

    @Query("SELECT * FROM group_topics WHERE group_id = :groupId AND tag_id=:tagId ORDER BY date_created DESC LIMIT 100")
    LiveData<List<GroupTopic>> getGroupTopics(String groupId, String tagId);

    @Query("SELECT * FROM group_topics WHERE group_id = :groupId ORDER BY date_created DESC LIMIT 100")
    LiveData<List<GroupTopic>> getGroupTopics(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addGroupTopic(GroupTopic topic);

    @Query("SELECT * FROM group_topics WHERE id=:topicId")
    LiveData<GroupTopic> getGroupTopic(String topicId);
}
