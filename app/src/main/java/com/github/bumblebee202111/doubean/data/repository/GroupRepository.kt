package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.COLUMN_VALUE_GROUP_TAG_ID_ALL
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItem
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.db.model.toSimpleGroupWithColor
import com.github.bumblebee202111.doubean.data.paging.GroupSearchResultItemPagingSource
import com.github.bumblebee202111.doubean.data.paging.GroupTagTopicRemoteMediator
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.asEntity
import com.github.bumblebee202111.doubean.network.model.toCachedGroupEntity
import com.github.bumblebee202111.doubean.network.model.toGroupDetail
import com.github.bumblebee202111.doubean.network.model.toGroupItemWithMemberInfo
import com.github.bumblebee202111.doubean.network.model.toSimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.network.util.loadCacheAndRefresh
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import com.github.bumblebee202111.doubean.util.RESULT_GROUPS_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class GroupRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val ApiService: ApiService,
) {
    private val groupDao = appDatabase.groupDao()

    fun getGroup(id: String) =
        loadCacheAndRefresh(
            getCache = { groupDao.getCachedGroup(id) },
            mapCacheToCacheDomain = { it.toSimpleGroupWithColor() },
            fetchRemote = {
                ApiService.getGroup(id)
            },
            saveCache = {
                groupDao.insertCachedGroup(it.toCachedGroupEntity())
                groupDao.insertGroupTabs(it.tabs.map { it.asEntity(id) })
            },
            mapResponseToDomain = {
                it.toGroupDetail()
            },
        )

    @OptIn(ExperimentalPagingApi::class)
    fun search(query: String) = Pager(
        PagingConfig(
            pageSize = RESULT_GROUPS_COUNT,
            prefetchDistance = RESULT_GROUPS_COUNT / 2,
            initialLoadSize = RESULT_GROUPS_COUNT
        ),
        pagingSourceFactory = {
            GroupSearchResultItemPagingSource(
            query = query, service = ApiService, appDatabase = appDatabase
            )
        }
    ).flow

    @OptIn(ExperimentalPagingApi::class)
    fun getTopicsPagingData(groupId: String, tagId: String?, sortBy: TopicSortBy) = Pager(
        PagingConfig(
            pageSize = RESULT_TOPICS_PAGE_SIZE,
            prefetchDistance = RESULT_TOPICS_PAGE_SIZE / 2,
            initialLoadSize = RESULT_TOPICS_PAGE_SIZE
        ),
        remoteMediator = GroupTagTopicRemoteMediator(
            groupId = groupId,
            tagId = tagId,
            sortBy = sortBy, service = ApiService, appDatabase = appDatabase
        ),
        pagingSourceFactory = {
            groupDao.groupTagTopicPagingSource(
                groupId = groupId,
                tagId = tagId ?: COLUMN_VALUE_GROUP_TAG_ID_ALL,
                sortBy = sortBy
            )
        }
    ).flow.map { it.map(PopulatedTopicItem::asExternalModel) }

    suspend fun getDayRanking(): AppResult<List<GroupItemWithIntroInfo>> {
        return makeApiCall(
            apiCall = {
                ApiService.getDayRanking()
            },
            mapSuccess = { data ->
                groupDao.upsertSimpleCachedGroups(data.items.map { it.group.toSimpleCachedGroupPartialEntity() })
                data.items.map {
                    it.group.toGroupItemWithMemberInfo()
                }
            },

            )
    }

    companion object {
        const val RESULT_TOPICS_PAGE_SIZE = 40
    }
}




