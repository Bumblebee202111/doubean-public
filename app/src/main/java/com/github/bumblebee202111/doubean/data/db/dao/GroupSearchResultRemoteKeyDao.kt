package com.github.bumblebee202111.doubean.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultRemoteKey

@Dao
interface GroupSearchResultRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: GroupSearchResultRemoteKey)

    @Query("SELECT * FROM group_search_result_remote_keys WHERE `query` = :query")
    suspend fun remoteKeyByQuery(query: String): GroupSearchResultRemoteKey

    @Query("DELETE FROM group_search_result_remote_keys WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)
}