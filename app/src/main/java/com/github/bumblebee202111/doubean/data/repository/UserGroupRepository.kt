package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.data.db.AppDatabase
import com.github.bumblebee202111.doubean.data.db.model.FavoriteEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFavoriteItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.RecommendedTopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.SimpleGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import com.github.bumblebee202111.doubean.data.db.model.asExternalModel
import com.github.bumblebee202111.doubean.model.GroupFavoriteItem
import com.github.bumblebee202111.doubean.model.TopicItemWithGroup
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.model.getRequestParamString
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
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_POST_NOTIFICATIONS = 5

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

    suspend fun addFavoriteGroup(
        groupId: String,
        enablePostNotifications: Boolean,
        allowsDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: TopicSortBy,
        feedRequestPostCountLimit: Int,
    ) {
        userGroupDao.insertFavoriteGroup(
            FavoriteGroupEntity(
                groupId = groupId,
                enableTopicNotifications = enablePostNotifications,
                allowDuplicateNotifications = allowsDuplicateNotifications,
                sortRecommendedTopicsBy = sortRecommendedPostsBy,
                feedRequestTopicCountLimit = feedRequestPostCountLimit
            )
        )
    }

    suspend fun removeFavoriteTab(tabId: String) {
        userGroupDao.deleteFavoriteTab(tabId)
    }

    suspend fun addFavoriteTab(
        groupId: String,
        tabId: String,
        enablePostNotifications: Boolean,
        allowsDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: TopicSortBy,
        feedRequestPostCountLimit: Int,
    ) {
        userGroupDao.insertFavoriteTab(
            FavoriteGroupTabEntity(
                groupId = groupId,
                tabId = tabId,
                enableTopicNotifications = enablePostNotifications,
                allowDuplicateNotifications = allowsDuplicateNotifications,
                sortRecommendedTopicsBy = sortRecommendedPostsBy,
                feedRequestTopicCountLimit = feedRequestPostCountLimit
            )
        )
    }

    fun getAllGroupFavorites(): Flow<List<GroupFavoriteItem>> =
        userGroupDao.loadAllFavorites().map {
            it.map(PopulatedGroupFavoriteItem::asExternalModel)
        }

    suspend fun getRecommendedPosts(): Boolean {

        val leastRecentlyNotifiedGroup = userGroupDao.getLeastRecentlyNotifiedGroup()
        val leastRecentlyNotifiedTab = userGroupDao.getLeastRecentlyNotifiedTab()
        if (leastRecentlyNotifiedGroup == null && leastRecentlyNotifiedTab == null)//No follows yet
            return true
        val allPossibleLeastRecentlyFeedTopics = mutableListOf<FavoriteEntity>()
        leastRecentlyNotifiedGroup?.let {
            allPossibleLeastRecentlyFeedTopics += it
        }
        leastRecentlyNotifiedTab?.let {
            allPossibleLeastRecentlyFeedTopics += it
        }
        val currentFeedTopic =
            allPossibleLeastRecentlyFeedTopics.minBy { it.lastNotifiedTimeMillis }

        return try {
            val response = when (currentFeedTopic) {
                is FavoriteGroupEntity -> apiService.getGroupTopics(
                    groupId = currentFeedTopic.groupId,
                    sortBy = currentFeedTopic.sortRecommendedTopicsBy.getRequestParamString(),
                    count = currentFeedTopic.feedRequestTopicCountLimit
                )

                is FavoriteGroupTabEntity -> apiService.getGroupTopics(
                    groupId = currentFeedTopic.groupId,
                    topicTagId = currentFeedTopic.tabId,
                    sortBy = currentFeedTopic.sortRecommendedTopicsBy.getRequestParamString(),
                    count = currentFeedTopic.feedRequestTopicCountLimit
                )
            }
            val networkPosts = response.items.filterIsInstance<NetworkTopicItem>()
            val groupId = currentFeedTopic.groupId
            val posts = networkPosts.map { it.asPartialEntity(groupId) }.also {
                if (currentFeedTopic.sortRecommendedTopicsBy == TopicSortBy.TOP_LAST_CREATED)
                    it.sortedByDescending(TopicItemPartialEntity::lastUpdated)
            }

            val existingPostNotifications = userGroupDao.getRecommendedTopicNotifications()

            val networkUpdatedPosts = if (currentFeedTopic.allowDuplicateNotifications) {
                networkPosts.filter { networkPost ->
                    existingPostNotifications.find { e ->
                        e.topicId == networkPost.id && e.notifiedLastUpdated != networkPost.lastUpdated
                    } != null
                }
            } else emptyList()
            val networkNewPosts = networkPosts.filter {
                it.id !in existingPostNotifications.map(
                    RecommendedTopicNotificationEntity::topicId
                )
            }

            val truncatedPostIds = networkPosts.filter {
                it.id in (networkUpdatedPosts + networkNewPosts).map(
                    NetworkTopicItem::id
                )
            }
                .take(MAX_NUM_POST_NOTIFICATIONS).map(NetworkTopicItem::id)

            val truncatedNetworkUpdatedPosts =
                networkUpdatedPosts.filter { it.id in truncatedPostIds }
            val truncatedNetworkNewPosts = networkNewPosts.filter { it.id in truncatedPostIds }

            val postNotificationEntities = truncatedNetworkUpdatedPosts
                .map {
                    RecommendedTopicNotificationEntity(
                        topicId = it.id,
                        notifiedLastUpdated = it.lastUpdated,
                        isNotificationUpdated = true
                    )
                } + truncatedNetworkNewPosts
                .map {
                    RecommendedTopicNotificationEntity(
                        topicId = it.id,
                        notifiedLastUpdated = it.lastUpdated
                    )
                }

            val topicTags = networkPosts.flatMap { it.topicTags }.map { it.asEntity(groupId) }
            val authors = networkPosts.map { it.author.asEntity() }
            appDatabase.withTransaction {
                topicDao.upsertTopics(posts)
                groupDao.insertTopicTags(topicTags)
                userDao.insertUsers(authors)
                when (currentFeedTopic) {
                    is FavoriteGroupEntity -> userGroupDao.updateFavoritedGroupLastNotifiedTimeMillis(
                        currentFeedTopic.groupId,
                        System.currentTimeMillis()
                    )

                    is FavoriteGroupTabEntity -> userGroupDao.updateFavoritedGroupLastNotifiedTimeMillis(
                        currentFeedTopic.tabId,
                        System.currentTimeMillis()
                    )
                }
                userGroupDao.insertRecommendedTopicNotifications(postNotificationEntities)
            }
            val truncatedPosts =
                topicDao.loadOrderedTopicsWithGroups(truncatedPostIds).first()
                    .map(PopulatedTopicItemWithGroup::asExternalModel)
            if (truncatedPosts.isNotEmpty()) {
                notifier.postRecommendedPostNotifications(truncatedPosts)
            }
            true
        } catch (e: IOException) {
            false
        }


    }

    fun getRecommendedPostNotifications(): Flow<PagingData<TopicItemWithGroup>> {
        val pagingConfig = PagingConfig(NOTIFICATION_PAGE_SIZE)

        return Pager(pagingConfig) {
            userGroupDao.recommendedTopicNotificationsPagingSource()
        }.flow.map { pagingData ->
            pagingData.map(PopulatedRecommendedTopicNotificationItem::asExternalModel)
        }
    }


    suspend fun updateGroupNotificationsPref(
        groupId: String,
        enableGroupNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: TopicSortBy,
        numberOfPostsLimitEachFeedFetch: Int,
    ) {
        userGroupDao.updateFavoritedGroupNotificationsPref(
            groupId,
            enableGroupNotifications,
            allowDuplicateNotifications,
            sortRecommendedPostsBy,
            numberOfPostsLimitEachFeedFetch
        )
    }

    suspend fun updateTabNotificationsPref(
        tabId: String,
        enableGroupNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: TopicSortBy,
        numberOfPostsLimitEachFeedFetch: Int,
    ) {
        userGroupDao.updateFollowedTabNotificationsPref(
            tabId,
            enableGroupNotifications,
            allowDuplicateNotifications,
            sortRecommendedPostsBy,
            numberOfPostsLimitEachFeedFetch
        )
    }

    companion object {
        const val NOTIFICATION_PAGE_SIZE = 20
    }
}