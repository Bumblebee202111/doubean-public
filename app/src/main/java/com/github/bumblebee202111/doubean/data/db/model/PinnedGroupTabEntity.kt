package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "pinned_group_tabs",
)
data class PinnedGroupTabEntity(
    @PrimaryKey
    @ColumnInfo("tab_id")
    val tabId: String,
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("pinned_date")
    val pinnedDate: Calendar = Calendar.getInstance(),
)

