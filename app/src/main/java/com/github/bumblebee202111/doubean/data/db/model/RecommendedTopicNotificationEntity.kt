package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity("recommended_topic_notifications")
data class RecommendedTopicNotificationEntity(
    @PrimaryKey
    @ColumnInfo("topic_id")
    val topicId: String,

    @ColumnInfo("notified_last_updated")
    val notifiedLastUpdated: LocalDateTime,

    val isNotificationUpdated: Boolean = false,
)