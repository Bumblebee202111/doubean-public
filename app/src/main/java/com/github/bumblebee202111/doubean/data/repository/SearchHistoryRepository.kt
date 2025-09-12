package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.dao.SearchHistoryDao
import com.github.bumblebee202111.doubean.data.db.model.SearchHistoryEntity
import com.github.bumblebee202111.doubean.data.db.model.toSearchHistory
import com.github.bumblebee202111.doubean.model.search.SearchHistory
import com.github.bumblebee202111.doubean.model.search.SearchType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(appDatabase: AppDatabase) {
    private val searchHistoryDao: SearchHistoryDao = appDatabase.searchHistoryDao()

    fun getHistory(type: SearchType): Flow<List<SearchHistory>> =
        searchHistoryDao.getHistory(type.value).map { entityList ->
            entityList.map { it.toSearchHistory() }
        }

    suspend fun addSearchTerm(type: SearchType, query: String) {
        searchHistoryDao.delete(query, type.value)
        val entry =
            SearchHistoryEntity(query = query, type = type.value, timestamp = LocalDateTime.now())
        searchHistoryDao.insert(entry)
    }

    suspend fun deleteSearchTerm(type: SearchType, query: String) {
        searchHistoryDao.delete(query, type.value)
    }

    suspend fun clearHistory(type: SearchType) {
        searchHistoryDao.clearAll(type.value)
    }
}