package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_topic_popular_comments")
data class GroupTopicPopularCommentEntity(
    @PrimaryKey
    @ColumnInfo("comment_id")
    val commentId: String,
    @ColumnInfo("topic_id")
    val topicId: String,
    val position: Int,
)