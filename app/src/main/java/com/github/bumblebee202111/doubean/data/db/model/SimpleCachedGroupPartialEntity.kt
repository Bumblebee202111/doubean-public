package com.github.bumblebee202111.doubean.data.db.model

import com.github.bumblebee202111.doubean.model.groups.SimpleGroup

data class SimpleCachedGroupPartialEntity(
    val id: String,
    val name: String,
    val url: String,
    val uri: String,
    val avatar: String,
)

fun SimpleCachedGroupPartialEntity.toSimpleGroup() = SimpleGroup(
    id = id, name = name, url = url, uri = uri, avatar = avatar
)








