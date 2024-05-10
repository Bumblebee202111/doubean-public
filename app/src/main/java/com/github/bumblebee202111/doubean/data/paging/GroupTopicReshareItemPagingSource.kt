package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.bumblebee202111.doubean.network.ApiKtorService
import com.github.bumblebee202111.doubean.network.model.NetworkReshareItem

class GroupTopicReshareItemPagingSource(
    private val apiService: ApiKtorService,
    private val topicId: String,
) :
    PagingSource<Int, NetworkReshareItem>() {

    override val jumpingSupported: Boolean = true
    override fun getRefreshKey(state: PagingState<Int, NetworkReshareItem>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkReshareItem> {
        val start = params.key ?: 0
        val count = params.loadSize
        try {
            val response = apiService.getGroupTopicResharesStatuses(
                topicId = topicId,
                start = start,
                count = count
            )

            val prevKey = if (start == 0) null else (start - params.loadSize).coerceAtLeast(0)
            val nextKey = (start + params.loadSize).takeIf { it < response.total }
            return LoadResult.Page(
                data = response.items,
                prevKey = prevKey,
                nextKey = nextKey,
                itemsBefore = start,
                itemsAfter = response.total - start - response.items.size
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}