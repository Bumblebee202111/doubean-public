package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectInterestWithSubject
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType

class UserSubjectInterestItemPagingSource(
    private val backend: ApiService,
    val userId: String,
    val subjectType: SubjectType,
    val initialStart: Int = 0,
) :
    PagingSource<Int, NetworkSubjectInterestWithSubject>() {
    override fun getRefreshKey(state: PagingState<Int, NetworkSubjectInterestWithSubject>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkSubjectInterestWithSubject> {
        return safePagingLoad {
            val start = params.key ?: initialStart
            val count = params.loadSize

            val response = backend.getUserSubjectInterests(
                userId = userId,
                type = subjectType.toNetworkSubjectType().value,
                start = start,
                count = count
            )

            val nextKey = when {
                response.total > 0 -> if (start + count < response.total) start + count else null
                response.interests.size < count -> null
                else -> start + count
            }

            LoadResult.Page(
                data = response.interests,
                prevKey = if (start == initialStart) null else start - count,
                nextKey = nextKey
            )
        }
    }

}