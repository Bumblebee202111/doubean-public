package com.doubean.ford.model

import com.doubean.ford.api.model.SortByRequestParam

enum class PostSortBy {
    LAST_UPDATED, NEW, TOP, NEW_TOP
}

fun PostSortBy.getRequestParamString() =
    when (this) {
        PostSortBy.LAST_UPDATED, PostSortBy.NEW -> SortByRequestParam.NEW
        PostSortBy.TOP, PostSortBy.NEW_TOP -> SortByRequestParam.TOP
    }.toString()