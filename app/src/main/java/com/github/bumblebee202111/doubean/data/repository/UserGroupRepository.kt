package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFavoriteItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.SimpleGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.data.repository.GroupRepository.Companion.RESULT_TOPICS_PAGE_SIZE
import com.github.bumblebee202111.doubean.model.GroupFavoriteItem
import com.github.bumblebee202111.doubean.model.GroupNotificationPreferences
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.model.getRequestParamString
import com.github.bumblebee202111.doubean.model.toGroupNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.model.toGroupTabNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkRecentTopicsFeedItem
import com.github.bumblebee202111.doubean.network.model.NetworkTopicItem
import com.github.bumblebee202111.doubean.network.model.NetworkTopicItemWithGroup
import com.github.bumblebee202111.doubean.network.model.asEntity
import com.github.bumblebee202111.doubean.network.model.asPartialEntity
import com.github.bumblebee202111.doubean.network.model.toEntity
import com.github.bumblebee202111.doubean.network.model.toGroupPartialEntity
import com.github.bumblebee202111.doubean.network.model.toTopicPartialEntity
import com.github.bumblebee202111.doubean.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_TOPIC_NOTIFICATIONS = 5

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

    fun getUserJoinedGroups(userId: String) = offlineFirstApiResultFlow(
        loadFromDb = {
            userGroupDao.getUserJoinedGroups(userId)
                .map { it.map(SimpleGroupPartialEntity::asExternalModel) }
        },
        call = {
            apiService.getUserJoinedGroups(userId)
        },
        saveSuccess = {
            appDatabase.withTransaction {
                userGroupDao.deleteAllUserJoinedGroupIds()
                groupDao.upsertJoinedGroups(groups.map { it.asPartialEntity() })
                userGroupDao.insertUserJoinedGroupIds(groups.mapIndexed { index, group ->
                    UserJoinedGroupIdEntity(
                        userId = userId,
                        groupId = group.id,
                        index = index
                    )
                })
            }
        })

    suspend fun subscribeGroup(groupId: String) = suspendRunCatching {
        apiService.subscribeGroup(groupId)
    }

    suspend fun unsubscribeGroup(groupId: String) = suspendRunCatching {
        apiService.unsubscribeGroup(groupId)
    }

    fun getRecentTopicsFeed() = offlineFirstApiResultFlow(
        loadFromDb = {
            userGroupDao.getTopicsFeed()
                .map { it.map(PopulatedTopicItemWithGroup::asExternalModel) }
        },
        call = {
            apiService.getGroupUserRecentTopicsFeed()
        },
        saveSuccess = {
            val networkTopics = feeds
                .map(NetworkRecentTopicsFeedItem::topic)
                .filterIsInstance<NetworkTopicItemWithGroup>()
            val feedItemEntities = networkTopics.map(NetworkTopicItemWithGroup::toEntity)

            val topicPartialEntities = networkTopics.map { it.toTopicPartialEntity() }
            val groups = networkTopics.map { it.group.toGroupPartialEntity() }
            val userEntities = networkTopics.map { it.author.asEntity() }
            val topicTagEntities =
                networkTopics.flatMap { topic -> topic.topicTags.map { it.asEntity(topic.group.id) } }
                    .distinctBy { it.id }

            appDatabase.withTransaction {
                userGroupDao.apply {
                    deleteTopicsFeed()
                    insertTopicsFeed(feedItemEntities)
                }
                topicDao.upsertTopics(topicPartialEntities)
                userDao.insertUsers(userEntities)
                groupDao.apply {
                    upsertTopicItemGroups(groups)
                    insertTopicTags(topicTagEntities)
                }
            }
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
                        count = currentNotificationTarget.maxTopicNotificationsPerFetch
                    )
                }
            }
            val networkTopics = response.items.filterIsInstance<NetworkTopicItem>()
            val groupId = currentNotificationTarget.groupId
            val topics = networkTopics.map { it.asPartialEntity(groupId) }.also {
                if (currentNotificationTarget.sortBy == TopicSortBy.HOT_LAST_CREATED ||
                    currentNotificationTarget.sortBy == TopicSortBy.NEW_LAST_CREATED
                )
                    it.sortedByDescending(TopicItemPartialEntity::createTime)
            }

            val existingTopicNotifications = userGroupDao.getTopicNotifications()

            val networkUpdatedTopics =
                if (currentNotificationTarget.notifyOnUpdates) {
                    networkTopics.filter { networkTopic ->
                        existingTopicNotifications.find { existingTopicNotifications ->
                            existingTopicNotifications.topicId == networkTopic.id && existingTopicNotifications.updateTime != networkTopic.updateTime
                        } != null
                    }
                } else emptyList()
            val networkNewTopics = networkTopics.filter {
                it.id !in existingTopicNotifications.map(
                    TopicNotificationEntity::topicId
                )
            }

            val truncatedTopicIds = networkTopics.filter {
                it.id in (networkUpdatedTopics + networkNewTopics).map(
                    NetworkTopicItem::id
                )
            }
                .take(currentNotificationTarget.maxTopicNotificationsPerFetch)
                .map(NetworkTopicItem::id)

            val truncatedNetworkUpdatedTopics =
                networkUpdatedTopics.filter { it.id in truncatedTopicIds }
            val truncatedNetworkNewTopics = networkNewTopics.filter { it.id in truncatedTopicIds }

            val now = LocalDateTime.now()

            val topicNotificationEntities = truncatedNetworkUpdatedTopics
                .map {
                    TopicNotificationEntity(
                        topicId = it.id,
                        updateTime = now,
                        isNotificationUpdated = true
                    )
                } + truncatedNetworkNewTopics
                .map {
                    TopicNotificationEntity(
                        topicId = it.id,
                        updateTime = now
                    )
                }

            val topicTags = networkTopics.flatMap { it.topicTags }.map { it.asEntity(groupId) }
            val authors = networkTopics.map { it.author.asEntity() }
            appDatabase.withTransaction {
                topicDao.upsertTopics(topics)
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
            val truncatedTopics =
                topicDao.loadOrderedTopicsWithGroups(truncatedTopicIds).first()
                    .map(PopulatedTopicItemWithGroup::asExternalModel)
            if (truncatedTopics.isNotEmpty()) {
                notifier.postTopicNotifications(truncatedTopics)
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