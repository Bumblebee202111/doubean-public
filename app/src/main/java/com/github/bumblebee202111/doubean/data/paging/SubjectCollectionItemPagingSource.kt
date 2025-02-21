package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.bumblebee202111.doubean.model.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toSubjectWithRankAndInterest

class SubjectCollectionItemPagingSource(
    private val backend: ApiService,
    val collectionId: String,
) :
    PagingSource<Int, SubjectWithRankAndInterest<*>>() {
    override fun getRefreshKey(state: PagingState<Int, SubjectWithRankAndInterest<*>>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SubjectWithRankAndInterest<*>> {
        return try {
            val start = params.key ?: 0
            val count = params.loadSize

            val response = backend.getSubjectCollectionItems(
                collectionId = collectionId,
                start = start,
                count = count
            )

            val nextKey = when {
                response.total > 0 -> if (start + count < response.total) start + count else null
                response.items.size < count -> null
                else -> start + count
            }

            LoadResult.Page(
                data = response.items.mapIndexed { index, item ->
                    item.toSubjectWithRankAndInterest(start + index + 1)
                },
                prevKey = if (start == 0) null else start - count,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}