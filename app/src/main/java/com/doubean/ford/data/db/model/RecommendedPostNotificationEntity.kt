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

    @ColumnInfo("post_last_updated")
    val postLastUpdated: LocalDateTime,

    val isNotificationUpdated: Boolean = false,
)