package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.annotation.StringRes
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy

enum class SortTopicsByOption(@StringRes val textRes: Int, val sortBy: TopicSortBy) {
    NEW_LAST_CREATED(R.string.sort_topics_by_new_last_created, TopicSortBy.NEW_LAST_CREATED),
    NEW(R.string.sort_topics_by_new, TopicSortBy.NEW),
    HOT_LAST_CREATED(R.string.sort_topics_by_hot_last_created, TopicSortBy.HOT_LAST_CREATED),
    HOT(R.string.sort_topics_by_hot, TopicSortBy.HOT),
}