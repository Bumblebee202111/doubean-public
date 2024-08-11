package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.TopicSortBy
import java.util.Calendar

@Entity(
    tableName = "favorite_groups",
)
data class FavoriteGroupEntity(
    @PrimaryKey
    @ColumnInfo("group_id")
    override val groupId: String,

    @ColumnInfo("favorite_date")
    override val favoriteDate: Calendar = Calendar.getInstance(),

    @ColumnInfo("feed_request_topic_count_limit") override val feedRequestTopicCountLimit: Int,

    @ColumnInfo("enable_notifications")
    override val enableTopicNotifications: Boolean,

    @ColumnInfo("allow_duplicate_notifications")
    override val allowDuplicateNotifications: Boolean,

    @ColumnInfo("sort_recommended_topics_by")
    override val sortRecommendedTopicsBy: TopicSortBy,

    @ColumnInfo("last_notified_time_millis")
    override val lastNotifiedTimeMillis: Long = 0,
) : FavoriteEntity