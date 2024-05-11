package com.github.bumblebee202111.doubean.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultRemoteKey
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.asPartialEntity

@OptIn(ExperimentalPagingApi::class)
class GroupSearchResultItemRemoteMediator(
    private val query: String,
    private val service: ApiService,
    private val appDatabase: AppDatabase,
) : RemoteMediator<Int, GroupSearchResultGroupItemPartialEntity>() {
    private val groupDao = appDatabase.groupDao()
    private val groupSearchResultRemoteKeyDao = appDatabase.groupSearchResultRemoteKeyDap()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GroupSearchResultGroupItemPartialEntity>,
    ): MediatorResult {
        val start = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> appDatabase.withTransaction {
                groupSearchResultRemoteKeyDao.remoteKeyByQuery(query)
            }.nextKey ?: return MediatorResult.Success(
                endOfPaginationReached = true
            )
        }
        return try {
            val response = service.searchGroups(
                query,
                start,
                if (start == 0) state.config.initialLoadSize else state.config.pageSize
            )
            val nextKey = (start + response.items.size).takeIf { it < response.total }
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    groupSearchResultRemoteKeyDao.deleteByQuery(query)
                    groupDao.deleteSearchResultPagingItemsByQuery(query)
                }
                groupSearchResultRemoteKeyDao.insert(GroupSearchResultRemoteKey(query, nextKey))
                groupDao.apply {
                    upsertSearchResultGroups(response.items.map { it.group.asPartialEntity() })
                    insertGroupSearchResultItems(response.items.mapIndexed { index, networkItem ->
                        GroupSearchResultItemEntity(query, start + index, networkItem.group.id)
                    })
                }
            }
            MediatorResult.Success(
                endOfPaginationReached = nextKey == null
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    }


}
