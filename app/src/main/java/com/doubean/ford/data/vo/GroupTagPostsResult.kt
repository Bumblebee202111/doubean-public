package com.doubean.ford.data.vo

import androidx.room.Entity

@Entity(tableName = "group_tag_posts_results", primaryKeys = ["tagId", "sortBy"])
data class GroupTagPostsResult(
    val groupId: String,
    val tagId: String,
    val sortBy: PostSortBy,
    override val ids: List<String>,
    override val totalCount: Int,
    override val next: Int?
) : ListResult