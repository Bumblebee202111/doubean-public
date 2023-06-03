package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "followed_groups",
)
data class FollowedGroupEntity(
    @PrimaryKey
    @ColumnInfo("group_id")
    val groupId: String,

    @ColumnInfo("follow_date")
    val followDate: Calendar = Calendar.getInstance(),
)