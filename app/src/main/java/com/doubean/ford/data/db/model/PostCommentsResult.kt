package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_comments_results")
class PostCommentsResult(
    @PrimaryKey val postId: String,
    @ColumnInfo("top_ids")
    val topIds: List<String>,
    @ColumnInfo("all_ids")
    val allIds: List<String>,
    @ColumnInfo("total_count")
    override val totalCount: Int,
    override val next: Int?,
) : PagedListResult {
    override val ids: List<String>
        get() = allIds
}