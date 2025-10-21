package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupUserTopicFeedItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PinnedGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPinnedTabItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.SimpleCachedGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UserGroupDao {

    @Query(
        """SELECT cached_groups.* FROM user_joined_group_ids LEFT JOIN cached_groups
ON user_joined_group_ids.group_id = cached_groups.id WHERE user_id=:userId ORDER BY `index`"""
    )
    fun getUserJoinedGroups(userId: String): Flow<List<SimpleCachedGroupPartialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserJoinedGroupIds(userJoinedGroupIds: List<UserJoinedGroupIdEntity>)

    @Query("DELETE FROM user_joined_group_ids")
    fun deleteAllUserJoinedGroupIds()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun pinTab(pinnedTab: PinnedGroupTabEntity)

    @Query("DELETE FROM pinned_group_tabs WHERE tab_id=:tabId")
    suspend fun unpinTab(tabId: String)

    @Query("SELECT EXISTS(SELECT * FROM pinned_group_tabs WHERE tab_id=:tabId)")
    fun isTabPinned(tabId: String): Flow<Boolean>

    @Transaction
    @Query(
        """
        SELECT
        pinned_group_tabs.pinned_date AS pinned_date,
        cached_groups.id AS group_id,
        cached_groups.name AS group_name,
        cached_groups.avatar AS group_avatar,
        pinned_group_tabs.tab_id AS tab_id,
        group_tabs.name AS tab_name
        FROM pinned_group_tabs
        LEFT OUTER JOIN group_tabs
        ON pinned_group_tabs.tab_id = group_tabs.id
        LEFT OUTER JOIN cached_groups
        ON cached_groups.id = group_tabs.group_id
        ORDER BY pinned_date
        """
    )
    fun getPinnedTabsWithGroupInfo(): Flow<List<PopulatedPinnedTabItem>>

    @Query("SELECT * FROM group_notification_group_targets WHERE group_id = :groupId")
    fun loadGroupNotificationTarget(groupId: String): Flow<GroupGroupNotificationTargetEntity?>

    @Query("SELECT * FROM group_notification_tab_targets WHERE tab_id = :tabId")
    fun loadTopicNotificationTarget(tabId: String): Flow<GroupTabNotificationTargetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicNotifications(topicNotifications: List<TopicNotificationEntity>)

    @Transaction
    @Query("SELECT * FROM topic_notifications ORDER BY time DESC")
    fun topicNotificationsPagingSource(): PagingSource<Int, PopulatedTopicNotificationItem>

    @Transaction
    @Query("SELECT * FROM topic_notifications WHERE topic_id IN (:ids) ORDER BY time DESC")
    fun getTopicsAndNotifications(ids: List<String>): List<PopulatedTopicNotificationItem>

    @Upsert(entity = GroupGroupNotificationTargetEntity::class)
    suspend fun upsertGroupNotificationTargetPreferences(
        preferences: GroupGroupNotificationTargetPartialEntity,
    )

    @Upsert(entity = GroupTabNotificationTargetEntity::class)
    suspend fun upsertTabNotificationTargetPreferences(
        preferences: GroupTabNotificationTargetPartialEntity,
    )

    @Query(
        """
        UPDATE group_notification_group_targets SET
        last_fetched_time_millis = :lastFetchedTimeMillis
        WHERE group_id=:groupId
    """
    )
    suspend fun updateGroupNotificationGroupTargetLastFetchedTimeMillis(
        groupId: String,
        lastFetchedTimeMillis: Long,
    )

    @Query(
        """
        UPDATE group_notification_tab_targets SET
        last_fetched_time_millis = :lastFetchedTimeMillis
        WHERE group_id=:groupId AND tab_id = :tabId
    """
    )
    suspend fun updateGroupNotificationTabTargetLastFetchedTimeMillis(
        groupId: String,
        tabId: String?,
        lastFetchedTimeMillis: Long,
    )

    @Query("SELECT * FROM group_notification_group_targets WHERE notifications_enabled = 1 ORDER BY last_fetched_time_millis ASC LIMIT 1")
    suspend fun getLeastRecentlyFetchedGroupNotificationTarget(): GroupGroupNotificationTargetEntity?

    @Query("SELECT * FROM group_notification_tab_targets WHERE notifications_enabled = 1 ORDER BY last_fetched_time_millis ASC LIMIT 1")
    suspend fun getLeastRecentlyFetchedTabNotificationTarget(): GroupTabNotificationTargetEntity?

    @Transaction
    @Query(
        """
SELECT * FROM group_user_topic_feed_items LEFT JOIN topics
ON group_user_topic_feed_items.id == topics.id
ORDER BY create_time DESC
"""
    )
    fun getTopicsFeed(): Flow<List<PopulatedTopicItemWithGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicsFeed(topicFeedItems: List<GroupUserTopicFeedItemEntity>)

    @Query("DELETE FROM group_user_topic_feed_items")
    suspend fun deleteTopicsFeed()


}