package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkGroupTopicComment

class GroupTopicCommentPagingSource(
    private val apiService: ApiService,
    private val topicId: String,
    private val spmId: String? = null,
    private val onPopularCommentsFetched: (List<NetworkGroupTopicComment>) -> Unit,
) :
    PagingSource<Int, NetworkGroupTopicComment>() {

    override val jumpingSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, NetworkGroupTopicComment>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkGroupTopicComment> {
        return safePagingLoad {
            val start = params.key ?: 0
            val count = params.loadSize
            val response = apiService.getGroupTopicComments(
                topicId = topicId,
                start = start,
                count = count,
                spmId = spmId
            )

            response.popularComments.filterIsInstance<NetworkGroupTopicComment>()
                .takeIf { it.isNotEmpty() }?.let(onPopularCommentsFetched)

            val realComments = response.comments.filterIsInstance<NetworkGroupTopicComment>()

            val prevKey = if (start == 0) null else (start - count).coerceAtLeast(0)
            val nextKey = (start + count).takeIf { it < response.total }

            LoadResult.Page(
                data = realComments,
                prevKey = prevKey,
                nextKey = nextKey,
                itemsBefore = start,
                itemsAfter = response.total - start - realComments.size
            )
        }
    }

}
