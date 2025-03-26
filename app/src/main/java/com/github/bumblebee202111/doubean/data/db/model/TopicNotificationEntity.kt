package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("topic_notifications")
data class TopicNotificationEntity(
    @PrimaryKey
    @ColumnInfo("topic_id")
    val topicId: String,
    val time: Long,
    val isNotificationUpdated: Boolean = false,
)