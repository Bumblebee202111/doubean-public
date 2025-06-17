package com.github.bumblebee202111.doubean.model.common

import com.github.bumblebee202111.doubean.model.doulists.MyCollectedItemDouList
import java.time.LocalDateTime

data class DouListPostItem(
    val feedItem: FeedItem<*>,
    val collectionReason: String,
    val collectionTime: LocalDateTime,
    val douList: MyCollectedItemDouList?, // null for items in a single Doulist
)
