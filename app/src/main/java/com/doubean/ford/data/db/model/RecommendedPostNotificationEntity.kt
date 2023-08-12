package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity("recommended_post_notifications")
data class RecommendedPostNotificationEntity(
    @PrimaryKey
    @ColumnInfo("post_id")
    val postId: String,

    @ColumnInfo("notified_last_updated")
    val notifiedLastUpdated: LocalDateTime,

    val isNotificationUpdated: Boolean = false,
)