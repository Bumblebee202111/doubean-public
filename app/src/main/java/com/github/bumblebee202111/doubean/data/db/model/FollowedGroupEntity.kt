package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.PostSortBy
import java.util.Calendar

@Entity(
    tableName = "followed_groups",
)
data class FollowedGroupEntity(
    @PrimaryKey
    @ColumnInfo("group_id")
    override val groupId: String,

    @ColumnInfo("follow_date")
    override val followDate: Calendar = Calendar.getInstance(),

    @ColumnInfo("feed_request_post_count_limit")
    override val feedRequestPostCountLimit: Int,

    @ColumnInfo("enable_notifications")
    override val enablePostNotifications: Boolean,

    @ColumnInfo("allow_duplicate_notifications")
    override val allowDuplicateNotifications: Boolean,

    @ColumnInfo("sort_recommended_posts_by")
    override val sortRecommendedPostsBy: PostSortBy,

    @ColumnInfo("last_notified_time_millis")
    override val lastNotifiedTimeMillis: Long = 0,
) : FollowableEntity