package com.doubean.ford.data.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_comments_results")
class PostCommentsResult(
    @PrimaryKey val postId: String,
    override val ids: List<String>,
    override val totalCount: Int,
    override val next: Int?
) : ListResult