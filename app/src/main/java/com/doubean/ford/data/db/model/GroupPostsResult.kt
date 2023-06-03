package com.doubean.ford.data.db.model

import androidx.room.*
import com.doubean.ford.data.db.Converters
import com.doubean.ford.model.PostSortBy

@Entity(primaryKeys = ["group_id", "sort_by"])
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