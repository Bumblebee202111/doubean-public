package com.doubean.ford.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.doubean.ford.data.vo.GroupFollowed;

import java.util.List;

/**
 * The Data Access Object for the [GroupFollowed] class.
 */
@Dao
public interface GroupFollowedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFollowed(GroupFollowed groupFollowed);

    @Query("DELETE FROM group_followed WHERE group_id=:groupId AND (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL ))")
    void deleteFollowed(String groupId, String groupTabId);

    @Query("SELECT * FROM group_followed")
    LiveData<List<GroupFollowed>> getFollowedIds();

    @Query("SELECT EXISTS(SELECT * FROM group_followed WHERE group_id= :groupId and  (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL )))")
    LiveData<Boolean> getFollowed(String groupId, String groupTabId);

}
