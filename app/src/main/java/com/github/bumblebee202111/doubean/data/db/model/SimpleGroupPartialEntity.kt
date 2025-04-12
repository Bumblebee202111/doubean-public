package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import com.github.bumblebee202111.doubean.model.groups.GroupItem

data class SimpleGroupPartialEntity(
    val id: String,
    val name: String,
    @ColumnInfo("avatar_url")
    val avatarUrl: String?,
)

fun SimpleGroupPartialEntity.asExternalModel(): GroupItem {
    return GroupItem(
        id = id, name = name, avatarUrl = avatarUrl
    )
}