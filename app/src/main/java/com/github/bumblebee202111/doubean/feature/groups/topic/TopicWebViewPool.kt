package com.github.bumblebee202111.doubean.feature.groups.topic

import android.content.Context
import android.webkit.WebView
import com.github.bumblebee202111.doubean.ui.component.RexxarWebViewPool


object TopicWebViewPool {
    private fun key(topicId: String) = "topic_$topicId"

    fun isPreloaded(topicId: String): Boolean =
        RexxarWebViewPool.isPreloaded(key(topicId))

    fun getOrCreate(
        topicId: String,
        context: Context,
        factory: (Context) -> WebView = { WebView(it) },
    ): WebView = RexxarWebViewPool.getOrCreate(key(topicId), context, factory)

    fun detachFromWindow(topicId: String) =
        RexxarWebViewPool.detachFromWindow(key(topicId))

    fun release(topicId: String) =
        RexxarWebViewPool.release(key(topicId))

    fun destroyAll() =
        RexxarWebViewPool.destroyAll()
}