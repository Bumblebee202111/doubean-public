package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_joined_group_ids")
data class UserJoinedGroupIdEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("user_id")
    val userId: String,
    @ColumnInfo("group_id")
    val groupId: String,
    val index: Int,
)