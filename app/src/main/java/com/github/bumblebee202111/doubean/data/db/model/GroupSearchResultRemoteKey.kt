package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("group_search_result_remote_keys")
data class GroupSearchResultRemoteKey(
    @PrimaryKey
    val query: String,
    val nextKey: Int?,
)