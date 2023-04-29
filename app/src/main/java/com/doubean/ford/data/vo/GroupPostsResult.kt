package com.doubean.ford.data.vo

import androidx.room.*
import com.doubean.ford.data.db.Converters

@Entity(primaryKeys = ["groupId", "sortBy"])
@TypeConverters(Converters::class)
class GroupPostsResult(
    val groupId: String,
    val sortBy: PostSortBy,
    override val ids: List<String>,
    override val totalCount: Int,
    override val next: Int?
) : ListResult