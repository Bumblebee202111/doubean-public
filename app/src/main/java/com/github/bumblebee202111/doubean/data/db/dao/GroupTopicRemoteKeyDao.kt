package com.github.bumblebee202111.doubean.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.bumblebee202111.doubean.data.db.model.GroupTabTopicRemoteKey
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy

@Dao
interface GroupTopicRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: GroupTabTopicRemoteKey)

    @Query("SELECT * FROM group_tab_topic_remote_keys WHERE group_id = :groupId AND tab_id = :tabId AND sort_by= :sortBy")
    suspend fun remoteKey(
        groupId: String,
        tabId: String,
        sortBy: TopicSortBy,
    ): GroupTabTopicRemoteKey

    @Query("DELETE FROM group_tab_topic_remote_keys WHERE group_id = :groupId AND tab_id = :tabId AND sort_by= :sortBy")
    suspend fun delete(groupId: String, tabId: String, sortBy: TopicSortBy)
}