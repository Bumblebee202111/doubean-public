package com.github.bumblebee202111.doubean.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.bumblebee202111.doubean.data.db.model.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity)

    @Query("SELECT * FROM search_history WHERE type = :type ORDER BY timestamp DESC LIMIT 15")
    fun getHistory(type: String): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE `query` = :query AND `type` = :type")
    suspend fun delete(query: String, type: String)

    @Query("DELETE FROM search_history WHERE type = :type")
    suspend fun clearAll(type: String)
}