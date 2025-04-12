package com.github.bumblebee202111.doubean.model.groups

import com.github.bumblebee202111.doubean.network.model.SortByRequestParam

enum class TopicSortBy {
    NEW, NEW_LAST_CREATED, HOT, HOT_LAST_CREATED
}

fun TopicSortBy.getRequestParamString() =
    when (this) {
        TopicSortBy.NEW, TopicSortBy.NEW_LAST_CREATED -> SortByRequestParam.NEW
        TopicSortBy.HOT, TopicSortBy.HOT_LAST_CREATED -> SortByRequestParam.TOP
    }.toString()