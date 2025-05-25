package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFavoriteItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.TopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.db.model.toGroupNotificationPreferences
import com.github.bumblebee202111.doubean.data.db.model.toSimpleGroup
import com.github.bumblebee202111.doubean.data.repository.GroupRepository.Companion.RESULT_TOPICS_PAGE_SIZE
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.groups.GroupFavoriteItem
import com.github.bumblebee202111.doubean.model.groups.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.groups.TopicItemWithGroup
import com.github.bumblebee202111.doubean.model.groups.TopicSortBy
import com.github.bumblebee202111.doubean.model.groups.getRequestParamString
import com.github.bumblebee202111.doubean.model.groups.toGroupNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.model.groups.toGroupTabNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkGroupTopicTag
import com.github.bumblebee202111.doubean.network.model.NetworkRecentTopicsFeedItem
import com.github.bumblebee202111.doubean.network.model.NetworkTopicItem
import com.github.bumblebee202111.doubean.network.model.NetworkTopicItemWithGroup
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.fangorns.toUserEntity
import com.github.bumblebee202111.doubean.network.model.toCachedGroupEntity
import com.github.bumblebee202111.doubean.network.model.toEntity
import com.github.bumblebee202111.doubean.network.model.toGroupItem
import com.github.bumblebee202111.doubean.network.model.toGroupTopicTagEntity
import com.github.bumblebee202111.doubean.network.model.toSimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.network.model.toTopicPartialEntity
import com.github.bumblebee202111.doubean.network.util.loadCacheAndRefresh
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import com.github.bumblebee202111.doubean.network.util.networkBoundResource
import com.github.bumblebee202111.doubean.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserGroupRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: ApiService,
    private val notifier: Notifier,
) {
    private val userGroupDao = appDatabase.userGroupDao()
    private val groupDao = appDatabase.groupDao()
    private val topicDao = appDatabase.groupTopicDao()
    private val userDao = appDatabase.userDao()

    fun getUserJoinedGroups(userId: String) = loadCacheAndRefresh(
        getCache = {
            userGroupDao.getUserJoinedGroups(userId)
        },
        mapCacheToCacheDomain = {
            it.map {
                it.toSimpleGroup()
            }
        },
        fetchRemote = {
            apiService.getUserJoinedGroups(userId)
        },
        saveCache = {
            appDatabase.withTransaction {
                userGroupDao.deleteAllUserJoinedGroupIds()
                groupDao.insertCachedGroups(it.groups.map { it.toCachedGroupEntity() })
                userGroupDao.insertUserJoinedGroupIds(it.groups.mapIndexed { index, group ->
                    UserJoinedGroupIdEntity(
                        userId = userId,
                        groupId = group.id,
                        index = index
                    )
                })
            }
        },
        mapResponseToDomain = {
            it.groups.map { it.toGroupItem() }
        })

    suspend fun subscribeGroup(groupId: String): AppResult<Unit> = makeApiCall(
        apiCall = { apiService.subscribeGroup(groupId) },
        mapSuccess = { }
    )

    suspend fun unsubscribeGroup(groupId: String): AppResult<Unit> = makeApiCall(
        apiCall = { apiService.unsubscribeGroup(groupId) },
        mapSuccess = { }
    )

    fun getRecentTopicsFeed() = networkBoundResource(
        queryDb = {
            userGroupDao.getTopicsFeed()
        },
        fetchRemote = {
            apiService.getGroupUserRecentTopicsFeed()
        },
        saveRemoteResponseToDb = { response ->
            val networkTopics = response.feeds
                .map(NetworkRecentTopicsFeedItem::topic)
                .filterIsInstance<NetworkTopicItemWithGroup>()
            val feedItemEntities = networkTopics.map(NetworkTopicItemWithGroup::toEntity)

            val topicPartialEntities = networkTopics.map { it.toTopicPartialEntity() }
            val groups = networkTopics.map { it.group.toSimpleCachedGroupPartialEntity() }
            val userEntities = networkTopics.map { it.author.toUserEntity() }
            val topicTagEntities =
                networkTopics.flatMap { topic ->
                    topic.topicTags.map {
                        it.toGroupTopicTagEntity(
                            topic.group.id
                        )
                    }
                }
                    .distinctBy { it.id }

            appDatabase.withTransaction {
                userGroupDao.apply {
                    deleteTopicsFeed()
                    insertTopicsFeed(feedItemEntities)
                }
                topicDao.upsertTopicItems(topicPartialEntities)
                userDao.insertUsers(userEntities)
                groupDao.apply {
                    upsertSimpleCachedGroups(groups)
                    insertTopicTags(topicTagEntities)
                }
            }
        },
        mapDbEntityToDomain = { entity ->
            entity.map(PopulatedTopicItemWithGroup::asExternalModel)
        }
    )

    fun getGroupFavorite(groupId: String): Flow<Boolean> {
        return userGroupDao.loadGroupFavorite(groupId)
    }

    fun getTabFavorite(tabId: String): Flow<Boolean> {
        return userGroupDao.loadTabFavorite(tabId)
    }

    suspend fun removeFavoriteGroup(groupId: String) {
        userGroupDao.deleteFavoriteGroup(groupId)
    }

    suspend fun addFavoriteGroup(groupId: String) {
        userGroupDao.insertFavoriteGroup(
            FavoriteGroupEntity(groupId = groupId)
        )
    }

    suspend fun removeFavoriteTab(tabId: String) {
        userGroupDao.deleteFavoriteTab(tabId)
    }

    suspend fun addFavoriteTab(
        groupId: String,
        tabId: String,

        ) {
        userGroupDao.insertFavoriteTab(
            FavoriteGroupTabEntity(groupId = groupId, tabId = tabId)
        )
    }

    fun getAllGroupFavorites(): Flow<List<GroupFavoriteItem>> =
        userGroupDao.loadAllFavorites().map {
            it.map(PopulatedGroupFavoriteItem::asExternalModel)
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

            // truncate early for better quality
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

    fun getTopicNotifications(): Flow<PagingData<TopicItemWithGroup>> {
        val pagingConfig = PagingConfig(NOTIFICATION_PAGE_SIZE)
        return Pager(pagingConfig) {
            userGroupDao.topicNotificationsPagingSource()
        }.flow.map { pagingData ->
            pagingData.map(PopulatedTopicNotificationItem::asExternalModel)
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

    companion object {
        const val NOTIFICATION_PAGE_SIZE = 20
    }
}