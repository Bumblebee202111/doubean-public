package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_top_comments")
data class PostTopComments(@PrimaryKey val postId: String, val commentIds: List<String>)