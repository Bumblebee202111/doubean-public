package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.doubean.ford.model.PostSortBy

@Entity(tableName = "group_tag_posts_results", primaryKeys = ["tag_id", "sort_by"])
data class GroupTagPostsResult(
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("tag_id")
    val tagId: String,
    @ColumnInfo("sort_by")
    val sortBy: PostSortBy,
    override val ids: List<String>,
    @ColumnInfo("total_count")
    override val totalCount: Int,
    override val next: Int?,
) : PagedListResult