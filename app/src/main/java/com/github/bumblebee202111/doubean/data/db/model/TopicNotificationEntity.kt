package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity("topic_notifications")
data class TopicNotificationEntity(
    @PrimaryKey
    @ColumnInfo("topic_id")
    val topicId: String,

    @ColumnInfo("update_time")
    val updateTime: LocalDateTime,

    val isNotificationUpdated: Boolean = false,
)