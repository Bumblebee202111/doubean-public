package com.github.bumblebee202111.doubean.feature.groups.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.COLUMN_VALUE_GROUP_TAG_ID_ALL
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.PinnedGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPinnedTabItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.TopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.db.model.toGroupNotificationPreferences
import com.github.bumblebee202111.doubean.data.db.model.toSimpleGroupWithColor
import com.github.bumblebee202111.doubean.feature.groups.model.PinnedTabItem
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.groups.GroupItemWithIntroInfo
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.model.groups.getRequestParamString
import com.github.bumblebee202111.doubean.model.groups.toGroupNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.model.groups.toGroupTabNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.network.api.GroupApiService
import com.github.bumblebee202111.doubean.network.model.NetworkTopicItem
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.NetworkGroupTopicTag
import com.github.bumblebee202111.doubean.network.model.fangorns.toGroupTopicTagEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserEntity
import com.github.bumblebee202111.doubean.network.model.toGroupItemWithMemberInfo
import com.github.bumblebee202111.doubean.network.model.toSimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.network.util.loadCacheAndRefresh
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import com.github.bumblebee202111.doubean.notifications.Notifier
import com.github.bumblebee202111.doubean.util.RESULT_GROUPS_COUNT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class GroupRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: GroupApiService,
    private val notifier: Notifier,
) {
    private val groupDao = appDatabase.groupDao()
    private val topicDao = appDatabase.groupTopicDao()
    private val userDao = appDatabase.userDao()
    private val userGroupDao = appDatabase.userGroupDao()

    fun getGroup(id: String) =
        loadCacheAndRefresh(
            getCache = { groupDao.getCachedGroup(id) },
            mapCacheToCacheDomain = { it.toSimpleGroupWithColor() },
            fetchRemote = {
                apiService.getGroup(id)
            },
            saveCache = {
                groupDao.insertCachedGroup(it.toCachedGroupEntity())
                groupDao.insertGroupTabs(it.tabs.map { tab -> tab.toGroupTabEntity(id) })
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
                query = query, apiService = apiService, appDatabase = appDatabase
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
            sortBy = sortBy, apiService = apiService, appDatabase = appDatabase
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
                apiService.getDayRanking()
            },
            mapSuccess = { data ->
                groupDao.upsertSimpleCachedGroups(data.items.map { it.group.toSimpleCachedGroupPartialEntity() })
                data.items.map {
                    it.group.toGroupItemWithMemberInfo()
                }
            }
        )
    }

    fun isTabPinned(tabId: String): Flow<Boolean> {
        return userGroupDao.isTabPinned(tabId)
    }

    suspend fun unpinTab(tabId: String) {
        userGroupDao.unpinTab(tabId)
    }

    suspend fun pinTab(
        groupId: String,
        tabId: String,

        ) {
        userGroupDao.pinTab(
            PinnedGroupTabEntity(groupId = groupId, tabId = tabId)
        )
    }

    fun getPinnedTabs(): Flow<List<PinnedTabItem>> =
        userGroupDao.getPinnedTabsWithGroupInfo().map {
            it.map(PopulatedPinnedTabItem::toPinnedTabItem)
        }

    fun getGroupNotificationPreferences(groupId: String): Flow<GroupNotificationPreferences?> {
        return userGroupDao.loadGroupNotificationTarget(groupId).map {
            it?.toGroupNotificationPreferences()
        }
    }

    fun getTabNotificationPreferences(tabId: String): Flow<GroupNotificationPreferences?> {
        return userGroupDao.loadTopicNotificationTarget(tabId).map {
            it?.toGroupNotificationPreferences()
        }
    }

    suspend fun updateGroupNotificationPreferences(
        groupId: String,
        preference: GroupNotificationPreferences,
    ) {
        userGroupDao.upsertGroupNotificationTargetPreferences(
            preference.toGroupNotificationTargetPartialEntity(groupId = groupId)
        )
    }

    suspend fun updateTabNotificationPreferences(
        groupId: String,
        tabId: String,
        preference: GroupNotificationPreferences,
    ) {
        userGroupDao.upsertTabNotificationTargetPreferences(
            preference.toGroupTabNotificationTargetPartialEntity(groupId = groupId, tabId = tabId)
        )
    }

    suspend fun getNextTargetTopicNotifications(): Boolean {
        val currentNotificationTarget = setOf(
            userGroupDao.getLeastRecentlyFetchedGroupNotificationTarget(),
            userGroupDao.getLeastRecentlyFetchedTabNotificationTarget()
        ).filterNotNull().minByOrNull { it.lastFetchedTimeMillis } ?: return true
        return try {
            val response = when (currentNotificationTarget) {
                is GroupGroupNotificationTargetEntity -> {
                    apiService.getGroupTopics(
                        groupId = currentNotificationTarget.groupId,
                        sortBy = currentNotificationTarget.sortBy.getRequestParamString(),
                        count = RESULT_TOPICS_PAGE_SIZE
                    )
                }

                is GroupTabNotificationTargetEntity -> {
                    apiService.getGroupTopics(
                        groupId = currentNotificationTarget.groupId,
                        topicTagId = currentNotificationTarget.tabId,
                        sortBy = currentNotificationTarget.sortBy.getRequestParamString(),
                        count = RESULT_TOPICS_PAGE_SIZE
                    )
                }
            }
            val networkTopics = response.items.filterIsInstance<NetworkTopicItem>()
            val groupId = currentNotificationTarget.groupId

            
            val networkTopicCandidates = networkTopics.also {
                if (currentNotificationTarget.sortBy == TopicSortBy.HOT_LAST_CREATED ||
                    currentNotificationTarget.sortBy == TopicSortBy.NEW_LAST_CREATED
                )
                    it.sortedByDescending(NetworkTopicItem::createTime)
            }.take(currentNotificationTarget.maxTopicNotificationsPerFetch)

            val existingTopicNotifications =
                userGroupDao.getTopicsAndNotifications(networkTopicCandidates.map { it.id })

            val finalNetworkUpdatedTopics =
                if (currentNotificationTarget.notifyOnUpdates) {
                    networkTopics.filter { networkTopic ->
                        existingTopicNotifications.find { existingTopicNotification ->
                            existingTopicNotification.topicWithGroup.partialEntity.id == networkTopic.id && existingTopicNotification.topicWithGroup.partialEntity.updateTime != networkTopic.updateTime
                        } != null
                    }
                } else emptyList()
            val finalNetworkNewTopics = networkTopics.filter { networkTopic ->
                networkTopic.id !in existingTopicNotifications.map { existingTopicNotification ->
                    existingTopicNotification.topicWithGroup.partialEntity.id
                }
            }

            val finalOrderedNetworkTopics = networkTopicCandidates.filter { candidate ->
                candidate.id in (finalNetworkUpdatedTopics + finalNetworkNewTopics).map { final -> final.id }
            }

            val topicNotificationEntities = finalNetworkUpdatedTopics
                .map {
                    TopicNotificationEntity(
                        topicId = it.id,
                        time = System.currentTimeMillis(),
                        isNotificationUpdated = true
                    )
                } + finalNetworkNewTopics.map {
                TopicNotificationEntity(
                    topicId = it.id,
                    time = System.currentTimeMillis()
                )
            }
            val finalTopicEntities = finalOrderedNetworkTopics.map { it.asPartialEntity(groupId) }

            val topicTags = finalOrderedNetworkTopics.flatMap(NetworkTopicItem::topicTags)
                .distinctBy(NetworkGroupTopicTag::id).map { it.toGroupTopicTagEntity(groupId) }
            val authors = finalOrderedNetworkTopics.map { it.author.toUserEntity() }
            appDatabase.withTransaction {
                topicDao.upsertTopicItems(finalTopicEntities)
                groupDao.insertTopicTags(topicTags)
                userDao.insertUsers(authors)
                when (currentNotificationTarget) {
                    is GroupGroupNotificationTargetEntity -> {
                        userGroupDao.updateGroupNotificationGroupTargetLastFetchedTimeMillis(
                            groupId = currentNotificationTarget.groupId,
                            lastFetchedTimeMillis = System.currentTimeMillis()
                        )
                    }

                    is GroupTabNotificationTargetEntity -> {
                        userGroupDao.updateGroupNotificationTabTargetLastFetchedTimeMillis(
                            groupId = currentNotificationTarget.groupId,
                            tabId = currentNotificationTarget.tabId,
                            lastFetchedTimeMillis = System.currentTimeMillis()
                        )
                    }
                }

                userGroupDao.insertTopicNotifications(topicNotificationEntities)
            }
            val finalTopics =
                topicDao.loadOrderedTopicsWithGroups(finalOrderedNetworkTopics.map(NetworkTopicItem::id))
                    .first()
                    .map(PopulatedTopicItemWithGroup::asExternalModel)
            if (finalTopics.isNotEmpty()) {
                notifier.postTopicNotifications(finalTopics)
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    companion object {
        const val RESULT_TOPICS_PAGE_SIZE = 40
    }
}