package com.github.bumblebee202111.doubean.shared.doulist.model

import com.github.bumblebee202111.doubean.model.common.FeedItem
import java.time.LocalDateTime

data class DouListPostItem(
    val feedItem: FeedItem<*>,
    val collectionReason: String,
    val collectionTime: LocalDateTime,
    val douList: MyCollectedItemDouList?, 
)