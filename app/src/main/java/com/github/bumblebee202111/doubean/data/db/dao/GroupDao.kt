package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.GroupDetailPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTagTopicItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTopicTagEntity
import com.github.bumblebee202111.doubean.data.db.model.JoinedGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupDetail
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedTopicItem
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupItemGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupTopic
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.data.db.model.TopicGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.TopicItemGroupPartialEntity
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.model.TopicSortBy
import kotlinx.coroutines.flow.Flow


@Dao
@RewriteQueriesToDropUnusedColumns
interface GroupDao {
    @Transaction
    @Query(value = "SELECT * FROM groups WHERE id = :groupId")
    fun getGroupDetail(groupId: String): Flow<PopulatedGroupDetail?>

    @Upsert(entity = GroupEntity::class)
    suspend fun upsertGroupDetail(group: GroupDetailPartialEntity)

    @Upsert(entity = GroupEntity::class)
    suspend fun upsertTopicGroup(group: TopicGroupPartialEntity)

    @Upsert(entity = GroupEntity::class)
    suspend fun upsertTopicItemGroups(groups: List<TopicItemGroupPartialEntity>)

    @Upsert(entity = GroupEntity::class)
    suspend fun upsertSearchResultGroupItems(groups: List<GroupSearchResultGroupItemPartialEntity>)

    @Upsert(entity = GroupEntity::class)
    suspend fun upsertRecommendedGroupItemGroups(groups: List<RecommendedGroupItemGroupPartialEntity>)

    @Upsert(entity = GroupEntity::class)
    fun upsertJoinedGroups(groups: List<JoinedGroupPartialEntity>)

    @Insert(GroupSearchResultItemEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupSearchResultItems(groupSearchResultItems: List<GroupSearchResultItemEntity>)

    @Transaction
    @Query("SELECT * FROM group_search_result_items LEFT OUTER JOIN groups ON group_search_result_items.group_id = groups.id WHERE `query` = :query ORDER BY `index` ASC")
    fun groupSearchResultPagingSource(query: String): PagingSource<Int, GroupSearchResultGroupItemPartialEntity>

    @Query("DELETE FROM group_search_result_items WHERE `query` = :query")
    fun deleteSearchResultPagingItemsByQuery(query: String)

    @Query("SELECT * FROM recommended_groups_results WHERE recommendation_type=:type")
    fun loadRecommendedGroups(type: GroupRecommendationType): Flow<RecommendedGroupsResult?>

    @Upsert(entity = RecommendedGroupEntity::class)
    suspend fun upsertRecommendedGroups(recommendedGroupEntities: List<RecommendedGroupEntity>)

    @Transaction
    @Query("SELECT * FROM recommended_groups WHERE group_id IN (:ids)")
    fun loadRecommendedGroups(ids: List<String>): Flow<List<PopulatedRecommendedGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedGroupsResult(recommendedGroupsResult: RecommendedGroupsResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTabs(tabs: List<GroupTabEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicTags(topicTags: List<GroupTopicTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedGroupTopics(recommendedGroupTopics: List<RecommendedGroupTopic>)

    @Query("DELETE FROM recommended_group_topics WHERE group_id IN (:groupIds)")
    suspend fun deleteRecommendedGroupTopicsByGroupIds(groupIds: List<String>)

    @Insert(GroupTagTopicItemEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupTopicItems(topicItems: List<GroupTagTopicItemEntity>)

    @Transaction
    @Query("""SELECT id, title, author_id, create_time, update_time, like_count, reaction_count, reshares_count, save_count, comments_count, short_content, content, cover_url, url, uri, topics.group_id, images, ip_location FROM group_tag_topics LEFT JOIN topics ON topic_id = topics.id WHERE group_tag_topics.group_id = :groupId AND group_tag_topics.tag_id = :tagId AND sort_by = :sortBy ORDER BY `index` ASC""")
    fun groupTagTopicPagingSource(
        groupId: String,
        tagId: String,
        sortBy: TopicSortBy,
    ): PagingSource<Int, PopulatedTopicItem>

    @Query("DELETE FROM group_tag_topics WHERE group_id = :groupId AND tag_id = :tagId AND sort_by = :sortBy")
    fun deleteGroupTagTopicItems(groupId: String, tagId: String, sortBy: TopicSortBy)

    @Query("UPDATE groups SET is_subscribed = :isSubscribed WHERE id=:id")
    suspend fun updateIsSubscribed(id: String, isSubscribed: Boolean?)

}