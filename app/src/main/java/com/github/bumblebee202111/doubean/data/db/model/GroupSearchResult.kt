package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_search_results")
class GroupSearchResult(
    @PrimaryKey val query: String, override val ids: List<String>, override val totalCount: Int,
    override val next: Int?,
) : PagedListResult