package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupUserTopicFeedItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFavoriteItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.RecommendedTopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.SimpleGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import com.github.bumblebee202111.doubean.model.TopicSortBy
import kotlinx.coroutines.flow.Flow


@Dao
interface UserGroupDao {

    @Query(
        """SELECT groups.* FROM user_joined_group_ids LEFT JOIN groups 
ON user_joined_group_ids.group_id = groups.id WHERE user_id=:userId ORDER BY `index`"""
    )
    fun getUserJoinedGroups(userId: String): Flow<List<SimpleGroupPartialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserJoinedGroupIds(userJoinedGroupIds: List<UserJoinedGroupIdEntity>)

    @Query("DELETE FROM user_joined_group_ids")
    fun deleteAllUserJoinedGroupIds()
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
    fun loadAllFavorites(): Flow<List<PopulatedGroupFavoriteItem>>

    @Query("SELECT * FROM recommended_topic_notifications")
    suspend fun getRecommendedTopicNotifications(): List<RecommendedTopicNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedTopicNotifications(recommendedTopicNotifications: List<RecommendedTopicNotificationEntity>)

    @Transaction
    @Query("SELECT * FROM recommended_topic_notifications ORDER BY notified_last_updated DESC")
    fun recommendedTopicNotificationsPagingSource(): PagingSource<Int, PopulatedRecommendedTopicNotificationItem>

    @Query("UPDATE favorite_groups SET enable_notifications = 0 WHERE group_id = :groupId")
    suspend fun disableFavoritedGroupNotifications(groupId: String)

    @Query("UPDATE favorite_groups SET enable_notifications = 1 WHERE group_id = :groupId")
    suspend fun enableGroupNotifications(groupId: String)

    @Query("UPDATE favorite_group_tabs SET enable_notifications = 0 WHERE tab_id = :tabId")
    suspend fun disableFollowedTabNotifications(tabId: String)

    @Query("UPDATE favorite_group_tabs SET enable_notifications = 1 WHERE tab_id = :tabId")
    suspend fun enableFollowedTabNotifications(tabId: String)

    @Query(
        """
        UPDATE favorite_groups SET 
        enable_notifications = :enableTopicNotifications, 
        allow_duplicate_notifications = :allowDuplicateNotifications, 
        sort_recommended_topics_by = :sortRecommendedTopicsBy, 
        feed_request_topic_count_limit = :feedRequestTopicCountLimit 
        WHERE group_id = :groupId
    """
    )
    suspend fun updateFavoritedGroupNotificationsPref(
        groupId: String,
        enableTopicNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedTopicsBy: TopicSortBy,
        feedRequestTopicCountLimit: Int,
    )

    @Query(
        """
        UPDATE favorite_group_tabs SET 
        enable_notifications = :enableTopicNotifications, 
        allow_duplicate_notifications = :allowDuplicateNotifications, 
        sort_recommended_topics_by = :sortRecommendedTopicsBy, 
        feed_request_topic_count_limit = :apiTopicCountLimitEachFeed 
        WHERE tab_id = :tabId
    """
    )
    suspend fun updateFollowedTabNotificationsPref(
        tabId: String,
        enableTopicNotifications: Boolean,
        allowDuplicateNotifications: Boolean,
        sortRecommendedTopicsBy: TopicSortBy,
        apiTopicCountLimitEachFeed: Int,
    )

    @Query(
        """
        UPDATE favorite_groups SET 
        last_notified_time_millis = :lastNotifiedTimeMillis 
        WHERE group_id = :groupId
    """
    )
    suspend fun updateFavoritedGroupLastNotifiedTimeMillis(
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
    @Query(
        """SELECT * FROM favorite_group_tabs
        WHERE enable_notifications = 1 
        ORDER BY last_notified_time_millis ASC LIMIT 1
    """
    )
    suspend fun getLeastRecentlyNotifiedTab(): FavoriteGroupTabEntity?

    @Transaction
    @Query(
        """
SELECT * FROM group_user_topic_feed_items LEFT JOIN topics 
ON group_user_topic_feed_items.id == topics.id 
ORDER BY created DESC 
"""
    )
    fun getTopicsFeed(): Flow<List<PopulatedTopicItemWithGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicsFeed(topicFeedItems: List<GroupUserTopicFeedItemEntity>)

    @Query("DELETE FROM group_user_topic_feed_items")
    suspend fun deleteTopicsFeed()


}