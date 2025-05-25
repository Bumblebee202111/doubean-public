package com.github.bumblebee202111.doubean.model.common

import com.github.bumblebee202111.doubean.model.fangorns.BaseShareObject

interface BaseFeedableItem : BaseShareObject {
    val id: String
    val uri: String
    val alt: String
    val type: String
    val layout: FeedItemLayout

    companion object {
        const val TYPE_TOPIC = "topic"
        const val TYPE_BOOK = "book"
        const val TYPE_MOVIE = "movie"
        const val TYPE_REVIEW = "review"

    }
}