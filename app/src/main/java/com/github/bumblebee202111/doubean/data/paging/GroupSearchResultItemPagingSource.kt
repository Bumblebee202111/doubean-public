package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toGroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.network.model.toSimpleCachedGroupPartialEntity

@OptIn(ExperimentalPagingApi::class)
class GroupSearchResultItemPagingSource(
    private val query: String,
    private val service: ApiService,
    private val appDatabase: AppDatabase,
) : PagingSource<Int, GroupItemWithIntroInfo>() {
    private val groupDao = appDatabase.groupDao()
    override fun getRefreshKey(state: PagingState<Int, GroupItemWithIntroInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, GroupItemWithIntroInfo> {
        val start = params.key ?: 0
        return try {
            val response = service.searchGroups(
                query,
                start,
                params.loadSize
            )
            groupDao.upsertSimpleCachedGroups(response.items.map { it.group.toSimpleCachedGroupPartialEntity() })
            val prevKey = if (start == 0) null else (start - params.loadSize).coerceAtLeast(0)
            val nextKey = (start + response.items.size).takeIf { it < response.total }
            LoadResult.Page(
                data = response.items.map { it.group.toGroupItemWithIntroInfo() },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }


}
