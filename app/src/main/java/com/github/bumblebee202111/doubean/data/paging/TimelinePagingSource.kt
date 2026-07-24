package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.bumblebee202111.doubean.data.prefs.PreferenceStorage
import com.github.bumblebee202111.doubean.model.common.FeedContent
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.network.api.FeedApiService
import com.github.bumblebee202111.doubean.network.model.common.toFeedItems
import kotlinx.coroutines.flow.firstOrNull

class TimelinePagingSource(
    private val apiService: FeedApiService,
    private val preferenceStorage: PreferenceStorage,
) : PagingSource<String, FeedItem<FeedContent>>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, FeedItem<FeedContent>> {
        return try {
            val maxId = params.key
            
            val lastVisitId =
                if (maxId == null) preferenceStorage.timelineLastVisitId.firstOrNull() else null

            val response = apiService.getTimeline(
                maxId = maxId,
                lastVisitId = lastVisitId,
                count = params.loadSize.coerceAtMost(20)
            )

            val feedItems = response.toFeedItems() 

            
            if (maxId == null && feedItems.isNotEmpty()) {
                val firstValidUid = feedItems.first().uid
                preferenceStorage.setTimelineLastVisitId(firstValidUid)
                
            }

            
            val nextKey = feedItems.lastOrNull()?.uid

            LoadResult.Page(
                data = feedItems,
                prevKey = null, 
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, FeedItem<FeedContent>>): String? = null
}