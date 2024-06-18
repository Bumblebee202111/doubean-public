package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupUserTopicFeedItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFavoriteItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedPostNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.RecommendedPostNotificationEntity
import com.github.bumblebee202111.doubean.model.TopicSortBy
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for Group related local user operations.
 */
@Dao
interface UserGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteGroup(favoriteGroup: FavoriteGroupEntity)

    @Query("DELETE FROM favorite_groups WHERE group_id=:groupId")
    suspend fun deleteFavoriteGroup(groupId: String)

    @Query("SELECT EXISTS(SELECT * FROM favorite_groups WHERE group_id= :groupId)")
    fun loadGroupFavorite(groupId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTab(favoriteGroupTab: FavoriteGroupTabEntity)

    @Query("DELETE FROM favorite_group_tabs WHERE tab_id=:tabId")
    suspend fun deleteFavoriteTab(tabId: String)

    @Query("SELECT EXISTS(SELECT * FROM favorite_group_tabs WHERE tab_id=:tabId)")
    fun loadTabFavorite(tabId: String): Flow<Boolean>

    @Transaction
    @Query(
        """
        SELECT 
        favorite_groups.favorite_date AS favorite_date, 
        favorite_groups.group_id AS group_id, 
        groups.name AS group_name, 
        groups.avatar_url AS group_avatar_url, 
        NULL AS tab_id, 
        NULL AS tab_name 
        FROM favorite_groups 
        LEFT OUTER JOIN groups 
        ON favorite_groups.group_id = groups.id 
        UNION ALL 
        SELECT 
        favorite_group_tabs.favorite_date AS favorite_date, 
        groups.id AS group_id,
        groups.name AS group_name, 
        groups.avatar_url AS group_avatar_url, 
        favorite_group_tabs.tab_id AS tab_id, 
        group_tabs.name AS tab_name 
        FROM favorite_group_tabs 
        LEFT OUTER JOIN group_tabs 
        ON favorite_group_tabs.tab_id = group_tabs.id 
        LEFT OUTER JOIN groups 
        ON groups.id = group_tabs.group_id 
        ORDER BY favorite_date
        """
    )
    @RewriteQueriesToDropUnusedColumns
    fun loadAllFavorites(): Flow<List<PopulatedGroupFavoriteItem>>

    @Query("SELECT * FROM recommended_post_notifications")
    suspend fun getRecommendedPostNotifications(): List<RecommendedPostNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedPostNotifications(recommendedPostNotifications: List<RecommendedPostNotificationEntity>)

    @Transaction
    @Query("SELECT * FROM recommended_post_notifications ORDER BY notified_last_updated DESC")
    fun recommendedPostNotificationsPagingSource(): PagingSource<Int, PopulatedRecommendedPostNotificationItem>

    @Query("UPDATE favorite_groups SET enable_notifications = 0 WHERE group_id = :groupId")
    suspend fun disableFollowedGroupNotifications(groupId: String)

    @Query("UPDATE favorite_groups SET enable_notifications = 1 WHERE group_id = :groupId")
    suspend fun enableGroupNotifications(groupId: String)

    @Query("UPDATE favorite_group_tabs SET enable_notifications = 0 WHERE tab_id = :tabId")
    suspend fun disableFollowedTabNotifications(tabId: String)

    @Query("UPDATE favorite_group_tabs SET enable_notifications = 1 WHERE tab_id = :tabId")
    suspend fun enableFollowedTabNotifications(tabId: String)

    @Query(
        """
        UPDATE favorite_groups SET 
        enable_notifications = :enablePostNotifications, 
        allow_duplicate_notifications = :allowDuplicateNotifications, 
        sort_recommended_posts_by = :sortRecommendedPostsBy, 
        feed_request_post_count_limit = :feedRequestPostCountLimit 
        WHERE group_id = :groupId
    """
    )
    suspend fun updateFollowedGroupNotificationsPref(
        groupId: String,
        enablePostNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: TopicSortBy,
        feedRequestPostCountLimit: Int,
    )

    @Query(
        """
        UPDATE favorite_group_tabs SET 
        enable_notifications = :enablePostNotifications, 
        allow_duplicate_notifications = :allowDuplicateNotifications, 
        sort_recommended_posts_by = :sortRecommendedPostsBy, 
        feed_request_post_count_limit = :apiPostCountLimitEachFeed 
        WHERE tab_id = :tabId
    """
    )
    suspend fun updateFollowedTabNotificationsPref(
        tabId: String,
        enablePostNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedPostsBy: TopicSortBy,
        apiPostCountLimitEachFeed: Int,
    )

    @Query(
        """
        UPDATE favorite_groups SET 
        last_notified_time_millis = :lastNotifiedTimeMillis 
        WHERE group_id = :groupId
    """
    )
    suspend fun updateFollowedGroupLastNotifiedTimeMillis(
        groupId: String,
        lastNotifiedTimeMillis: Long,
    )

    @Query(
        """
        UPDATE favorite_group_tabs SET 
        last_notified_time_millis = :lastNotifiedTimeMillis 
        WHERE tab_id = :tabId
    """
    )
    suspend fun updateFollowedTabLastNotifiedTimeMillis(tabId: String, lastNotifiedTimeMillis: Long)

    @Query("SELECT * FROM favorite_groups WHERE enable_notifications = 1 ORDER BY last_notified_time_millis ASC LIMIT 1")
    suspend fun getLeastRecentlyNotifiedGroup(): FavoriteGroupEntity?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM favorite_group_tabs
        WHERE enable_notifications = 1 
        ORDER BY last_notified_time_millis ASC LIMIT 1
    """
    )
    suspend fun getLeastRecentlyNotifiedTab(): FavoriteGroupTabEntity?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
SELECT * FROM group_user_topic_feed_items LEFT JOIN posts 
ON group_user_topic_feed_items.id == posts.id 
ORDER BY created DESC 
"""
    )
    fun getTopicsFeed(): Flow<List<PopulatedTopicItemWithGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicsFeed(topicFeedItems: List<GroupUserTopicFeedItemEntity>)

    @Query("DELETE FROM group_user_topic_feed_items")
    suspend fun deleteTopicsFeed()


}