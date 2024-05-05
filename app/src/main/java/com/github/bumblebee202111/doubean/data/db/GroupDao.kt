package com.github.bumblebee202111.doubean.data.db

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
import com.github.bumblebee202111.doubean.data.db.model.GroupPostsResult
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResult
import com.github.bumblebee202111.doubean.data.db.model.GroupSearchResultGroupItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTabEntity
import com.github.bumblebee202111.doubean.data.db.model.GroupTagPostsResult
import com.github.bumblebee202111.doubean.data.db.model.PopulatedGroupDetail
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostDetail
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.PopulatedRecommendedGroup
import com.github.bumblebee202111.doubean.data.db.model.PostDetailPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PostEntity
import com.github.bumblebee202111.doubean.data.db.model.PostGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PostItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PostTagCrossRef
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupItemGroupPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupPost
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupsResult
import com.github.bumblebee202111.doubean.model.GroupRecommendationType
import com.github.bumblebee202111.doubean.model.PostSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The Data Access Object for Group related operations.
 */
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

    @Query("SELECT * FROM group_search_results WHERE `query` = :query")
    fun loadSearchResult(query: String): Flow<GroupSearchResult?>

    @Query("SELECT * FROM group_search_results WHERE `query` = :query")
    suspend fun getSearchResult(query: String): GroupSearchResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupSearchResult(groupSearchResult: GroupSearchResult)

    @Query("SELECT * FROM groups WHERE id IN (:groupIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadSearchResultGroups(groupIds: List<String>): Flow<List<GroupSearchResultGroupItemPartialEntity>>

    fun loadOrderedSearchResultGroups(
        groupIds: List<String>,
        c: Comparator<GroupSearchResultGroupItemPartialEntity>? = null,
    ) = this.loadSearchResultGroups(groupIds).map { groups ->
        if (c == null) {
            groups.sortedWith(compareBy { o -> groupIds.indexOf(o.id) })
        } else groups.sortedWith(c)
        //groups
    }

    @Transaction
    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadPosts(postIds: List<String>): Flow<List<PopulatedPostItem>>
    fun loadOrderedPosts(postIds: List<String>) = loadPosts(postIds).map { posts ->
        posts.sortedWith(compareBy { o -> postIds.indexOf(o.partialEntity.id) })
    }

    @Transaction
    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadPostsWithGroups(postIds: List<String>): Flow<List<PopulatedPostItemWithGroup>>

    fun loadOrderedPostsWithGroups(postIds: List<String>) =
        loadPostsWithGroups(postIds).map { posts ->
            posts.sortedWith(compareBy { o -> postIds.indexOf(o.partialEntity.id) })
        }

    @Query("SELECT * FROM group_posts_result WHERE group_id = :groupId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupPosts(groupId: String, sortBy: PostSortBy): Flow<GroupPostsResult?>

    @Query("SELECT * FROM group_posts_result WHERE group_id = :groupId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getGroupPosts(groupId: String, sortBy: PostSortBy): GroupPostsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPostsResult(groupPostsResult: GroupPostsResult)

    @Query("SELECT * FROM group_tag_posts_results WHERE group_id = :groupId AND tag_id=:tagId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupTagPosts(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy,
    ): Flow<GroupTagPostsResult?>

    @Query("SELECT * FROM group_tag_posts_results WHERE group_id = :groupId AND tag_id=:tagId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getGroupTagPosts(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy,
    ): GroupTagPostsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupTagPostsResult(groupTagPostsResult: GroupTagPostsResult)

    @Insert(entity = PostEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostDetail(post: PostDetailPartialEntity)

    @Upsert(entity = PostEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertPosts(posts: List<PostItemPartialEntity>)

    @Transaction
    @Query("SELECT * FROM posts WHERE id=:postId")
    fun loadPost(postId: String): Flow<PopulatedPostDetail>


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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostTagCrossRefs(postTagCrossRefs: List<PostTagCrossRef>)

    @Query("DELETE FROM posts_tags WHERE post_id =:postId")
    suspend fun deletePostTagCrossRefsByPostId(postId: String)

    @Query("DELETE FROM posts_tags WHERE post_id IN (:postIds)")
    suspend fun deletePostTagCrossRefsByPostIds(postIds: List<String>)
}