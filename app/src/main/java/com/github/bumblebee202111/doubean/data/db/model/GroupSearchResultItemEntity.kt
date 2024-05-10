package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity("group_search_result_items", primaryKeys = ["query", "index"])
data class GroupSearchResultItemEntity(
    val query: String,
    val index: Int,
    @ColumnInfo("group_id")
    val groupId: String,
)