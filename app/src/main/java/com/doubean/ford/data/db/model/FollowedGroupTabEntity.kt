package com.doubean.ford.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "followed_group_tabs",
)
data class FollowedGroupTabEntity(
    @PrimaryKey
    @ColumnInfo("tab_id")
    val groupTabId: String,
    @ColumnInfo("follow_date")
    val followDate: Calendar = Calendar.getInstance(),
)