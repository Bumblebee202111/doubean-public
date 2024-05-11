package com.github.bumblebee202111.doubean.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.GroupDetailPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupPostTagEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultItemEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTagTopicItemEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupDetail
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedGroup
import com.github.bumblebee202111.doubean.data.db.model.PostGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupItemGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupPost
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.model.PostSortBy
import kotlinx.coroutines.flow.Flow


@Dao
interface GroupDao {
    @Transaction
    @Query(value = "SELECT * FROM groups WHERE id = :groupId")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupDetail(groupId: String): Flow<PopulatedGroupDetail?>

    @Transaction
    @Query(value = "SELECT * FROM groups WHERE id = :groupId")
    @RewriteQueriesToDropUnusedColumns
    fun getGroupDetail(groupId: String): PopulatedGroupDetail?

    @Upsert(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertGroupDetail(group: GroupDetailPartialEntity)

    @Update(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertPostGroup(group: PostGroupPartialEntity)

    @Upsert(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertSearchResultGroups(groupList: List<GroupSearchResultGroupItemPartialEntity>)

    @Upsert(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertRecommendedGroupItemGroups(groupList: List<RecommendedGroupItemGroupPartialEntity>)

    @Insert(GroupSearchResultItemEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupSearchResultItems(groupSearchResultItems: List<GroupSearchResultItemEntity>)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
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
    @RewriteQueriesToDropUnusedColumns
    fun loadRecommendedGroups(ids: List<String>): Flow<List<PopulatedRecommendedGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedGroupsResult(recommendedGroupsResult: RecommendedGroupsResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTabs(tabs: List<GroupTabEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPostTags(postTags: List<GroupPostTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendedGroupPosts(recommendedGroupPosts: List<RecommendedGroupPost>)

    @Query("DELETE FROM recommended_group_posts WHERE group_id IN (:groupIds)")
    suspend fun deleteRecommendedGroupPostByGroupIds(groupIds: List<String>)

    @Insert(GroupTagTopicItemEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupTopicItems(topicItems: List<GroupTagTopicItemEntity>)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT id, title, author_id, created, last_updated, like_count, reaction_count, repost_count, save_count, comment_count, short_content, content, cover_url, url, uri, posts.group_id, images, ip_location FROM group_tag_topics LEFT JOIN posts ON topic_id = posts.id WHERE group_tag_topics.group_id = :groupId AND group_tag_topics.tag_id = :tagId AND sort_by = :sortBy ORDER BY `index` ASC""")
    fun groupTagTopicPagingSource(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy,
    ): PagingSource<Int, PopulatedPostItem>

    @Query("DELETE FROM group_tag_topics WHERE group_id = :groupId AND tag_id = :tagId AND sort_by = :sortBy")
    fun deleteGroupTagTopicItems(groupId: String, tagId: String, sortBy: PostSortBy)

}