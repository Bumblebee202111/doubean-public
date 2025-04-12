package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.groups.GroupTopicTag

@Entity("group_topic_tags")
data class GroupTopicTagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo("group_id")
    val groupId: String,
)

fun GroupTopicTagEntity.asExternalModel() = GroupTopicTag(
    id = id,
    name = name
)