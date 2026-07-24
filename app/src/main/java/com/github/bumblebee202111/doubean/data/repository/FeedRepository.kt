package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.bumblebee202111.doubean.data.paging.TimelinePagingSource
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.model.common.FeedContent
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.network.api.FeedApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val apiService: FeedApiService,
    private val preferenceStorage: PreferenceStorage,
) {
    fun getTimelineStream(): Flow<PagingData<FeedItem<FeedContent>>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { TimelinePagingSource(apiService, preferenceStorage) }
        ).flow
    }
}