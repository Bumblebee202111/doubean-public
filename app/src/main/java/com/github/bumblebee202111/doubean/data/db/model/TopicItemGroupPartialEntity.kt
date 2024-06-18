package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.TopicItemGroup

data class TopicItemGroupPartialEntity(
    val id: String,

    val name: String,

    val url: String,

    val uri: String,

    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,

)

fun TopicItemGroupPartialEntity.asExternalModel() = TopicItemGroup(
    id = id,
    name = name,
    url = url,
    uri = uri,
    avatarUrl = avatarUrl
)