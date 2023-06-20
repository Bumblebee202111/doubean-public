package com.doubean.ford.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.doubean.ford.data.db.model.FollowedGroupEntity
import com.doubean.ford.data.db.model.FollowedGroupTabEntity
import com.doubean.ford.data.db.model.PopulatedGroupFollowItem

/**
 * The Data Access Object for Group related local user operations.
 */
@Dao
interface UserGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowedGroup(followedGroup: FollowedGroupEntity)

    @Query("DELETE FROM followed_groups WHERE group_id=:groupId")
    suspend fun deleteFollowedGroup(groupId: String)

    @Query("SELECT EXISTS(SELECT * FROM followed_groups WHERE group_id= :groupId)")
    fun loadGroupFollowed(groupId: String): LiveData<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowedTab(followedGroupTab: FollowedGroupTabEntity)

    @Query("DELETE FROM followed_group_tabs WHERE tab_id=:tabId")
    suspend fun deleteFollowedTab(tabId: String)

    @Query("SELECT EXISTS(SELECT * FROM followed_group_tabs WHERE tab_id=:tabId)")
    fun loadTabFollowed(tabId: String): LiveData<Boolean>

    @Transaction
    @Query("""
        SELECT 
        followed_groups.follow_date AS follow_date, 
        followed_groups.group_id AS group_id, 
        groups.name AS group_name, 
        groups.avatar_url AS group_avatar_url, 
        NULL AS tab_id, 
        NULL AS tab_name 
        FROM followed_groups 
        LEFT OUTER JOIN groups 
        ON followed_groups.group_id = groups.id 
        UNION ALL 
        SELECT 
        followed_group_tabs.follow_date AS follow_date, 
        groups.id AS group_id,
        groups.name AS group_name, 
        groups.avatar_url AS group_avatar_url, 
        followed_group_tabs.tab_id AS tab_id, 
        group_tabs.name AS tab_name 
        FROM followed_group_tabs 
        LEFT OUTER JOIN group_tabs 
        ON followed_group_tabs.tab_id = group_tabs.id 
        LEFT OUTER JOIN groups 
        ON groups.id = group_tabs.group_id 
        ORDER BY follow_date
        """)
    @RewriteQueriesToDropUnusedColumns
    fun loadAllFollows(): LiveData<List<PopulatedGroupFollowItem>>
}