package com.github.bumblebee202111.doubean.data.status

import com.github.bumblebee202111.doubean.core.network.model.common.NetworkNonAdFeedItem
import com.github.bumblebee202111.doubean.core.network.model.common.NetworkTimeline
import com.github.bumblebee202111.doubean.core.network.model.common.toFeedItem
import com.github.bumblebee202111.doubean.model.common.FeedContent
import com.github.bumblebee202111.doubean.model.common.FeedItem

fun NetworkTimeline.toFeedItems(): List<FeedItem<FeedContent>> {
    return items.filterIsInstance<NetworkNonAdFeedItem>().map { it.toFeedItem() }
}