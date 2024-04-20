package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.github.bumblebee202111.doubean.data.db.Converters
import com.github.bumblebee202111.doubean.model.PostSortBy

@Entity(tableName = "group_posts_result", primaryKeys = ["group_id", "sort_by"])
@TypeConverters(Converters::class)
class GroupPostsResult(
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("sort_by")
    val sortBy: PostSortBy,
    override val ids: List<String>,
    @ColumnInfo("total_count")
    override val totalCount: Int,
    override val next: Int?,
) : PagedListResult