package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.FavoriteGroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupGroupNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabNotificationTargetPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupUserTopicFeedItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupFavoriteItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicNotificationItem
import com.github.bumblebee202111.doubean.data.db.model.SimpleGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicNotificationEntity
import com.github.bumblebee202111.doubean.data.db.model.UserJoinedGroupIdEntity
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