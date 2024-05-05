package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_comments_remote_keys")
data class GroupTopicCommentsRemoteKey(
    @PrimaryKey
    @ColumnInfo("topic_id")
    val topicId: String,
    val nextKey: Int?,
)