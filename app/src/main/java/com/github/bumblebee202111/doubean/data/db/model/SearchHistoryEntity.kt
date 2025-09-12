package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.search.SearchHistory
import java.time.LocalDateTime

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val type: String,
    val timestamp: LocalDateTime,
)

fun SearchHistoryEntity.toSearchHistory(): SearchHistory {
    return SearchHistory(
        id = this.id,
        query = this.query
    )
}