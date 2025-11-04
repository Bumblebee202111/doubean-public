package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("group_tabs")
data class GroupTabEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val seq: Int,
    @ColumnInfo("group_id")
    val groupId: String,
)
