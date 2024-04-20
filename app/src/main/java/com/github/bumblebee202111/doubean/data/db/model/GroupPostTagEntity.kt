package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.GroupPostTag

@Entity("group_post_tags")
data class GroupPostTagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo("group_id")
    val groupId: String,
)

fun GroupPostTagEntity.asExternalModel() = GroupPostTag(
    id = id,
    name = name
)