package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "topics_tags",
    primaryKeys = ["topic_id", "tag_id"],
    indices = [Index(value = ["topic_id"]), Index(value = ["tag_id"])],
)
data class TopicTagCrossRef(
    @ColumnInfo("topic_id")
    val topicId: String,
    @ColumnInfo("tag_id")
    val tagId: String,
)