package com.github.bumblebee202111.doubean.feature.groups.topic.web.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.topic.web.TopicWebScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class TopicWebNavKey(
    val url: String,
) : NavKey

fun Navigator.navigateToTopicWeb(url: String) = navigate(key = TopicWebNavKey(url))

fun EntryProviderScope<NavKey>.topicWebEntry(onBackClick: () -> Unit) =
    entry<TopicWebNavKey> { key ->
        TopicWebScreen(
        key.url,
        onBackClick = onBackClick,
    )
}