package com.github.bumblebee202111.doubean.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.bumblebee202111.doubean.data.db.model.GroupTopicCommentsRemoteKey

@Dao
interface GroupTopicCommentRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: GroupTopicCommentsRemoteKey)

    @Query("SELECT * FROM topic_comments_remote_keys WHERE topic_id = :topicId")
    suspend fun getRemoteKey(topicId: String): GroupTopicCommentsRemoteKey

    @Query("DELETE FROM topic_comments_remote_keys WHERE topic_id = :topicId")
    suspend fun deleteRemoteKey(topicId: String)
}