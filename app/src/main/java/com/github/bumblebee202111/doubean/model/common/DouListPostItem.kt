package com.github.bumblebee202111.doubean.model.common

import java.time.LocalDateTime

data class DouListPostItem(
    val feedItem: FeedItem<*>,
    val collectionReason: String,
    val collectionTime: LocalDateTime,
)
