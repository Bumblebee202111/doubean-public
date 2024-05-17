package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.github.bumblebee202111.doubean.data.db.model.FollowedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FollowedGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFollowItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedPostNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.RecommendedPostNotificationEntity
import com.github.bumblebee202111.doubean.model.TopicSortBy
import kotlinx.coroutines.flow.Flow


@Dao
interface UserGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowedGroup(followedGroup: FollowedGroupEntity)

    @Query("DELETE FROM followed_groups WHERE group_id=:groupId")
    suspend fun deleteFollowedGroup(groupId: String)

    @Query("SELECT EXISTS(SELECT * FROM followed_groups WHERE group_id= :groupId)")
    fun loadGroupFollowed(groupId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowedTab(followedGroupTab: FollowedGroupTabEntity)

    @Query("DELETE FROM followed_group_tabs WHERE tab_id=:tabId")
    suspend fun deleteFollowedTab(tabId: String)

    @Query("SELECT EXISTS(SELECT * FROM followed_group_tabs WHERE tab_id=:tabId)")
    fun loadTabFollowed(tabId: String): Flow<Boolean>

    @Transaction
    @Query(
        """
        SELECT 
        followed_groups.follow_date AS follow_date, 
        followed_groups.group_id AS group_id, 
        groups.name AS group_name, 
        groups.avatar_url AS group_avatar_url, 
        NULL AS tab_id, 
        NULL AS tab_name 
        FROM followed_groups 
        LEFT OUTER JOIN groups 
        ON followed_groups.group_id = groups.id 
        UNION ALL 
        SELECT 
        followed_group_tabs.follow_date AS follow_date, 
        groups.id AS group_id,
        groups.name AS group_name, 
        groups.avatar_url AS group_avatar_url, 
        followed_group_tabs.tab_id AS tab_id, 
        group_tabs.name AS tab_name 
        FROM followed_group_tabs 
        LEFT OUTER JOIN group_tabs 
        ON followed_group_tabs.tab_id = group_tabs.id 
        LEFT OUTER JOIN groups 
        ON groups.id = group_tabs.group_id 
        ORDER BY follow_date
        """
    )
    @RewriteQueriesToDropUnusedColumns
    fun loadAllFollows(): Flow<List<PopulatedGroupFollowItem>>

    @Query("SELECT * FROM recommended_post_notifications")
    suspend fun getRecommendedPostNotifications(): List<RecommendedPostNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedPostNotifications(recommendedPostNotifications: List<RecommendedPostNotificationEntity>)

    @Transaction
    @Query("SELECT * FROM recommended_post_notifications ORDER BY notified_last_updated DESC")
    fun recommendedPostNotificationsPagingSource(): PagingSource<Int, PopulatedRecommendedPostNotificationItem>

    @Query("UPDATE followed_groups SET enable_notifications = 0 WHERE group_id = :groupId")
    suspend fun disableFollowedGroupNotifications(groupId: String)

    @Query("UPDATE followed_groups SET enable_notifications = 1 WHERE group_id = :groupId")
    suspend fun enableGroupNotifications(groupId: String)

    @Query("UPDATE followed_group_tabs SET enable_notifications = 0 WHERE tab_id = :tabId")
    suspend fun disableFollowedTabNotifications(tabId: String)

    @Query("UPDATE followed_group_tabs SET enable_notifications = 1 WHERE tab_id = :tabId")
    suspend fun enableFollowedTabNotifications(tabId: String)

    @Query(
        """
        UPDATE followed_groups SET 
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
        UPDATE followed_group_tabs SET 
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
        UPDATE followed_groups SET 
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
        UPDATE followed_group_tabs SET 
        last_notified_time_millis = :lastNotifiedTimeMillis 
        WHERE tab_id = :tabId
    """
    )
    suspend fun updateFollowedTabLastNotifiedTimeMillis(tabId: String, lastNotifiedTimeMillis: Long)

    @Query("SELECT * FROM followed_groups WHERE enable_notifications = 1 ORDER BY last_notified_time_millis ASC LIMIT 1")
    suspend fun getLeastRecentlyNotifiedGroup(): FollowedGroupEntity?

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM followed_group_tabs
        WHERE enable_notifications = 1 
        ORDER BY last_notified_time_millis ASC LIMIT 1
    """
    )
    suspend fun getLeastRecentlyNotifiedTab(): FollowedGroupTabEntity?
}