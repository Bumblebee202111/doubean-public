package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.github.bumblebee202111.doubean.model.PostSortBy

@Entity("group_tab_topic_remote_keys", primaryKeys = ["group_id", "tab_id", "sort_by"])
data class GroupTabTopicRemoteKey(
    @ColumnInfo("group_id")
    val groupId: String,
    @ColumnInfo("tab_id")
    val tabId: String,
    @ColumnInfo("sort_by")
    val sortBy: PostSortBy,
    val nextKey: Int?,
)