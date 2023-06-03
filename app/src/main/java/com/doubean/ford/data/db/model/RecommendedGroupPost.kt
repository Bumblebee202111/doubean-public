package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "recommended_group_posts",
    primaryKeys = ["group_id", "post_id"],
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["group_id"]),
        Index(value = ["post_id"]),
    ],
)
data class RecommendedGroupPost(
    @ColumnInfo(name = "group_id")
    val groupId: String,
    @ColumnInfo("post_id")
    val postId: String,
)