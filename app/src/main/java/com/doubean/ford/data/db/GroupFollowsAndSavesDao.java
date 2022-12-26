package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.doubean.ford.data.vo.GroupFollow;

import java.util.List;

/**
 * The Data Access Object for the [GroupFollow] class.
 */
@Dao
public interface GroupFollowsAndSavesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFollow(GroupFollow groupFollow);

    @Query("DELETE FROM group_follows WHERE group_id=:groupId AND (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL ))")
    void deleteFollow(String groupId, String groupTabId);

    @Query("SELECT * FROM group_follows")
    LiveData<List<GroupFollow>> loadFollowIds();

    @Query("SELECT EXISTS(SELECT * FROM group_follows WHERE group_id= :groupId and  (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL )))")
    LiveData<Boolean> loadFollowed(String groupId, String groupTabId);

}
