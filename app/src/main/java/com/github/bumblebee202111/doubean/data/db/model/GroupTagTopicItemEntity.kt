package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.github.bumblebee202111.doubean.model.PostSortBy

@Entity("group_tag_topics", primaryKeys = ["index", "group_id", "tag_id", "sort_by"])
data class GroupTagTopicItemEntity(
    @ColumnInfo("index")
    val index: Int,
    @ColumnInfo("topic_id")
    val topicId: String,
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("tag_id")
    val tagId: String,
    @ColumnInfo("sort_by")
    val sortBy: PostSortBy,
)
