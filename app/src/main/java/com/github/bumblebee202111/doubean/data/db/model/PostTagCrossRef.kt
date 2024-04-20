package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "posts_tags",
    primaryKeys = ["post_id", "tag_id"],
    indices = [Index(value = ["post_id"]), Index(value = ["tag_id"])],
)
class PostTagCrossRef(
    @ColumnInfo("post_id")
    val postId: String,
    @ColumnInfo("tag_id")
    val tagId: String,
)