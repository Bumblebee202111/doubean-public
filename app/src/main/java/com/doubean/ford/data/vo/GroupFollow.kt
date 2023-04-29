package com.doubean.ford.data.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_follows",
    indices = [Index(value = ["group_id", "group_tab_id"], unique = true)]
)
data class GroupFollow(
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "group_tab_id") val groupTabId: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int
) {
    val isGroup: Boolean
        get() = groupTabId == null
    val isGroupTab: Boolean
        get() = groupTabId != null
}