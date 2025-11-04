package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("group_user_topic_feed_items")
data class GroupUserTopicFeedItemEntity(
    @PrimaryKey
    val id: String,
)