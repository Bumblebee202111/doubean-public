package com.github.bumblebee202111.doubean.model.common

import com.github.bumblebee202111.doubean.network.model.common.CollectTypeRequestPaths

enum class CollectType {
    GROUP_TOPIC,
    MOVIE,
    TV,
    BOOK,
}

fun CollectType.toRequestPath(): String {
    return when (this) {
        CollectType.GROUP_TOPIC -> CollectTypeRequestPaths.GROUP_TOPIC
        CollectType.MOVIE -> CollectTypeRequestPaths.MOVIE
        CollectType.TV -> CollectTypeRequestPaths.TV
        CollectType.BOOK -> CollectTypeRequestPaths.BOOK
    }
}