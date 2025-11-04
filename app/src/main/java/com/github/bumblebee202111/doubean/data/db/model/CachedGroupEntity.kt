package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.groups.SimpleGroupWithColor


@Entity(tableName = "cached_groups")
data class CachedGroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val url: String,
    val uri: String,
    val avatar: String,


    val color: String? = null,
)

fun CachedGroupEntity.toSimpleGroupWithColor() = SimpleGroupWithColor(
    id = id, name = name, url = url, uri = uri, avatar = avatar, color = color
)








