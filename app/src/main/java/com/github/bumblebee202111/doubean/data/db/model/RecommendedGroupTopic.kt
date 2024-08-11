package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "recommended_group_topics",
    primaryKeys = ["group_id", "topic_id"],
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["group_id"]),
        Index(value = ["topic_id"]),
    ],
)
data class RecommendedGroupTopic(
    @ColumnInfo(name = "group_id")
    val groupId: String,
    @ColumnInfo("topic_id")
    val topicId: String,
)