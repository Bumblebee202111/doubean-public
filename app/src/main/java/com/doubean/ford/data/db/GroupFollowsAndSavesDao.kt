package com.doubean.ford.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.doubean.ford.data.vo.GroupFollow

/**
 * The Data Access Object for the [GroupFollow] class.
 */
@Dao
interface GroupFollowsAndSavesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFollow(groupFollow: GroupFollow)

    @Query("DELETE FROM group_follows WHERE group_id=:groupId AND (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL ))")
    fun deleteFollow(groupId: String, groupTabId: String)

    @Query("SELECT * FROM group_follows")
    fun loadFollowIds(): LiveData<List<GroupFollow>>

    @Query("SELECT EXISTS(SELECT * FROM group_follows WHERE group_id= :groupId and  (group_tab_id=:groupTabId OR ( group_tab_id IS NULL AND :groupTabId IS NULL )))")
    fun loadFollowed(groupId: String, groupTabId: String?): LiveData<Boolean>
}