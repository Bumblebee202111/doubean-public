package com.doubean.ford.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.doubean.ford.api.ApiSuccessResponse
import com.doubean.ford.api.DoubanService
import com.doubean.ford.api.model.NetworkPostItem
import com.doubean.ford.api.model.asEntity
import com.doubean.ford.api.model.asPartialEntity
import com.doubean.ford.data.db.AppDatabase
import com.doubean.ford.data.db.model.*
import com.doubean.ford.model.GroupFollowItem
import com.doubean.ford.model.PostSortBy
import com.doubean.ford.model.RecommendedPostNotificationItem
import com.doubean.ford.model.getRequestParamString
import com.doubean.ford.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val MAX_NUM_POST_NOTIFICATIONS = 5

class GroupUserDataRepository private constructor(
    private val appDatabase: AppDatabase,
    private val doubanService: DoubanService,
    private val notifier: Notifier,
) {
    private val userGroupDao = appDatabase.groupFollowsAndSavesDao()
    private val groupDao = appDatabase.groupDao()
    private val userDao = appDatabase.userDao()
    fun getGroupFollowed(groupId: String): Flow<Boolean> {
        return userGroupDao.loadGroupFollowed(groupId)
    }

    fun getTabFollowed(tabId: String): Flow<Boolean> {
        return userGroupDao.loadTabFollowed(tabId)
    }

    suspend fun removeFollowedGroup(groupId: String) {
        userGroupDao.deleteFollowedGroup(groupId)
    }

    suspend fun addFollowedGroup(
        groupId: String,
        enablePostNotifications: Boolean,
        allowsDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: PostSortBy,
        feedRequestPostCountLimit: Int,
    ) {
        userGroupDao.insertFollowedGroup(
            FollowedGroupEntity(
                groupId = groupId,
                enablePostNotifications = enablePostNotifications,
                allowDuplicateNotifications = allowsDuplicateNotifications,
                sortRecommendedPostsBy = sortRecommendedPostsBy,
                feedRequestPostCountLimit = feedRequestPostCountLimit
            )
        )
    }

    suspend fun removeFollowedTab(tabId: String) {
        userGroupDao.deleteFollowedTab(tabId)
    }

    suspend fun addFollowedTab(
        groupId: String,
        tabId: String,
        enablePostNotifications: Boolean,
        allowsDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: PostSortBy,
        feedRequestPostCountLimit: Int,
    ) {
        userGroupDao.insertFollowedTab(
            FollowedGroupTabEntity(
                groupId = groupId,
                tabId = tabId,
                enablePostNotifications = enablePostNotifications,
                allowDuplicateNotifications = allowsDuplicateNotifications,
                sortRecommendedPostsBy = sortRecommendedPostsBy,
                feedRequestPostCountLimit = feedRequestPostCountLimit
            )
        )
    }

    fun getAllGroupFollows(): Flow<List<GroupFollowItem>> =
        userGroupDao.loadAllFollows().map {
            it.map(PopulatedGroupFollowItem::asExternalModel)
        }

    suspend fun getRecommendedPosts(): Boolean {


        val leastRecentlyNotifiedGroup = userGroupDao.getLeastRecentlyNotifiedGroup()
        val leastRecentlyNotifiedTab = userGroupDao.getLeastRecentlyNotifiedTab()
        if (leastRecentlyNotifiedGroup == null && leastRecentlyNotifiedTab == null)//No follows yet
            return true
        val allPossibleLeastRecentlyFeedTopics = mutableListOf<FollowableEntity>()
        leastRecentlyNotifiedGroup?.let {
            allPossibleLeastRecentlyFeedTopics += it
        }
        leastRecentlyNotifiedTab?.let {
            allPossibleLeastRecentlyFeedTopics += it
        }
        val currentFeedTopic =
            allPossibleLeastRecentlyFeedTopics.minBy { it.lastNotifiedTimeMillis }

        val response = when (currentFeedTopic) {
            is FollowedGroupEntity -> doubanService.getGroupPosts(
                groupId = currentFeedTopic.groupId,
                sortBy = currentFeedTopic.sortRecommendedPostsBy.getRequestParamString(),
                count = currentFeedTopic.feedRequestPostCountLimit
            )
            is FollowedGroupTabEntity -> doubanService.getGroupPosts(
                groupId = currentFeedTopic.groupId,
                tagId = currentFeedTopic.tabId,
                sortBy = currentFeedTopic.sortRecommendedPostsBy.getRequestParamString(),
                count = currentFeedTopic.feedRequestPostCountLimit
            )
        }
        if (response is ApiSuccessResponse) {
            val networkPosts = response.body.items
            val groupId = currentFeedTopic.groupId
            val posts = networkPosts.map { it.asPartialEntity(groupId) }.also {
                if (currentFeedTopic.sortRecommendedPostsBy == PostSortBy.NEW_TOP)
                    it.sortedByDescending(PostItemPartialEntity::lastUpdated)
            }

            val existingPostNotifications = userGroupDao.getRecommendedPostNotifications()

            val networkUpdatedPosts = if (currentFeedTopic.allowDuplicateNotifications) {
                networkPosts.filter { networkPost ->
                    existingPostNotifications.find { e ->
                        e.postId == networkPost.id && e.notifiedLastUpdated != networkPost.lastUpdated
                    } != null
                }
            } else emptyList()
            val networkNewPosts = networkPosts.filter {
                it.id !in existingPostNotifications.map(
                    RecommendedPostNotificationEntity::postId
                )
            }

            val truncatedPostIds = networkPosts.filter {
                it.id in (networkUpdatedPosts + networkNewPosts).map(
                    NetworkPostItem::id
                )
            }
                .take(MAX_NUM_POST_NOTIFICATIONS).map(NetworkPostItem::id)

            val truncatedNetworkUpdatedPosts =
                networkUpdatedPosts.filter { it.id in truncatedPostIds }
            val truncatedNetworkNewPosts = networkNewPosts.filter { it.id in truncatedPostIds }

            val postNotificationEntities = truncatedNetworkUpdatedPosts
                .map {
                    RecommendedPostNotificationEntity(
                        postId = it.id,
                        notifiedLastUpdated = it.lastUpdated,
                        isNotificationUpdated = true
                    )
                } + truncatedNetworkNewPosts
                .map {
                    RecommendedPostNotificationEntity(
                        postId = it.id,
                        notifiedLastUpdated = it.lastUpdated
                    )
                }

            val postTags = networkPosts.flatMap { it.postTags }.map { it.asEntity(groupId) }
            val authors = networkPosts.map { it.author.asEntity() }
            appDatabase.withTransaction {
                groupDao.upsertPosts(posts)
                groupDao.insertGroupPostTags(postTags)
                userDao.insertUsers(authors)
                when (currentFeedTopic) {
                    is FollowedGroupEntity -> userGroupDao.updateFollowedGroupLastNotifiedTimeMillis(
                        currentFeedTopic.groupId,
                        System.currentTimeMillis()
                    )
                    is FollowedGroupTabEntity -> userGroupDao.updateFollowedGroupLastNotifiedTimeMillis(
                        currentFeedTopic.tabId,
                        System.currentTimeMillis()
                    )
                }
                userGroupDao.insertRecommendedPostNotifications(postNotificationEntities)
            }
            val truncatedPosts =
                groupDao.loadOrderedPostsWithGroups(truncatedPostIds).first()
                    .map(PopulatedPostItemWithGroup::asExternalModel)
            if (truncatedPosts.isNotEmpty()) {
                notifier.postRecommendedPostNotifications(truncatedPosts)
            }
            return true
        }
        return false
    }

    fun getRecommendedPostNotifications(): Flow<PagingData<RecommendedPostNotificationItem>> {
        val pagingConfig = PagingConfig(NOTIFICATION_PAGE_SIZE)

        return Pager(pagingConfig) {
            userGroupDao.recommendedPostNotificationsPagingSource()
        }.flow.map { pagingData ->
            pagingData.map(PopulatedRecommendedPostNotificationItem::asExternalModel)
        }
    }


    suspend fun updateGroupNotificationsPref(
        groupId: String,
        enableGroupNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: PostSortBy,
        numberOfPostsLimitEachFeedFetch: Int,
    ) {
        userGroupDao.updateFollowedGroupNotificationsPref(
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
        sortRecommendedPostsBy: PostSortBy,
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
        @Volatile
        private var instance: GroupUserDataRepository? = null
        const val NOTIFICATION_PAGE_SIZE = 20
        fun getInstance(
            appDatabase: AppDatabase,
            doubanService: DoubanService,
            notifier: Notifier,
        ): GroupUserDataRepository? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = GroupUserDataRepository(
                            appDatabase, doubanService, notifier
                        )
                    }
                }
            }
            return instance
        }
    }
}