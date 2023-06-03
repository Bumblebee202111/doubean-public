package com.doubean.ford.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import com.doubean.ford.data.db.model.*
import com.doubean.ford.model.GroupRecommendationType
import com.doubean.ford.model.PostSortBy

/**
 * The Data Access Object for Group related operations.
 */
@Dao
interface GroupDao {
    @Transaction
    @Query(value = "SELECT * FROM groups WHERE id = :groupId")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupDetail(groupId: String): LiveData<PopulatedGroupDetail>

    @Upsert(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertGroupDetail(group: GroupDetailPartialEntity)

    @Update(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertPostGroup(group: PostGroupPartialEntity)

    @Upsert(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertSearchResultGroups(groupList: List<GroupSearchResultGroupItemPartialEntity>)

    @Upsert(entity = GroupEntity::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertRecommendedGroupItemGroups(groupList: List<RecommendedGroupItemGroupPartialEntity>)

    @Query("SELECT * FROM group_search_results WHERE `query` = :query")
    fun search(query: String): LiveData<GroupSearchResult?>

    @Query("SELECT * FROM group_search_results WHERE `query` = :query")
    fun findSearchResult(query: String): GroupSearchResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupSearchResult(groupSearchResult: GroupSearchResult?)

    @Query("SELECT * FROM groups WHERE id IN (:groupIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadSearchResultGroupsById(groupIds: List<String>): LiveData<List<GroupSearchResultGroupItemPartialEntity>>

    fun loadOrderedSearchResultGroups(
        groupIds: List<String>,
        c: Comparator<GroupSearchResultGroupItemPartialEntity>? = null,
    ) = this.loadSearchResultGroupsById(groupIds).map { groups ->
        if (c == null) {
            groups.sortedWith(compareBy { o -> groupIds.indexOf(o.id) })
        } else groups.sortedWith(c)
        groups
    }

    @Transaction
    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadPostsById(postIds: List<String>): LiveData<List<PopulatedPostItem>>

    fun loadPosts(postIds: List<String>) = loadPostsById(postIds).map { posts ->
        posts.sortedWith(compareBy { o -> postIds.indexOf(o.partialEntity.id) })
    }


    @Query("SELECT * FROM GroupPostsResult WHERE group_id = :groupId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun getGroupPosts(groupId: String, sortBy: PostSortBy): LiveData<GroupPostsResult?>

    @Query("SELECT * FROM GroupPostsResult WHERE group_id = :groupId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun findGroupPosts(groupId: String, sortBy: PostSortBy): GroupPostsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupPostsResult(groupPostsResult: GroupPostsResult?)

    @Query("SELECT * FROM group_tag_posts_results WHERE group_id = :groupId AND tag_id=:tagId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun getGroupTagPosts(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy,
    ): LiveData<GroupTagPostsResult?>

    @Query("SELECT * FROM group_tag_posts_results WHERE group_id = :groupId AND tag_id=:tagId AND sort_by = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun findGroupTagPosts(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy,
    ): GroupTagPostsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupTagPostsResult(groupTagPostsResult: GroupTagPostsResult?)

    @Insert(entity = PostEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertPostDetail(post: PostDetailPartialEntity)

    @Upsert(entity = PostEntity::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertPosts(posts: List<PostItemPartialEntity>)

    @Transaction
    @Query("SELECT * FROM posts WHERE id=:postId")
    fun loadPost(postId: String): LiveData<PopulatedPostDetail>

    fun loadOrderedComments(
        commentIds: List<String>,
        c: Comparator<PopulatedPostComment>? = null,
    ): LiveData<List<PopulatedPostComment>> {
        return loadCommentsById(commentIds).map { comments ->
            if (c != null) comments.sortedWith(c) else comments.sortedWith(
                compareBy { o -> commentIds.indexOf(o.entity.id) })
            comments
        }
    }

    @Transaction
    @Query("SELECT * FROM post_comments WHERE id IN (:commentIds)")
    fun loadCommentsById(commentIds: List<String>): LiveData<List<PopulatedPostComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostComments(comments: List<PostCommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostCommentsResult(postCommentsResult: PostCommentsResult?)

    @Query("SELECT * FROM post_comments_results WHERE postId=:postId")
    fun getPostComments(postId: String): LiveData<PostCommentsResult?>

    @Query("SELECT * FROM post_comments_results WHERE postId=:postId")
    fun findPostComments(postId: String): PostCommentsResult?

    @Query("SELECT * FROM recommended_groups_results WHERE recommendation_type=:type")
    fun getRecommendedGroups(type: GroupRecommendationType): LiveData<RecommendedGroupsResult?>

    @Upsert(entity = RecommendedGroupEntity::class)
    fun upsertRecommendedGroups(recommendedGroupEntities: List<RecommendedGroupEntity?>): List<Long>

    @Transaction
    @Query("SELECT * FROM recommended_groups WHERE group_id IN (:ids)")
    @RewriteQueriesToDropUnusedColumns
    fun loadRecommendedGroupsByIds(ids: List<String>): LiveData<List<PopulatedRecommendedGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecommendedGroupsResult(recommendedGroupsResult: RecommendedGroupsResult?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupTabs(tabs: List<GroupTabEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupPostTags(postTags: List<GroupPostTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecommendedGroupPosts(recommendedGroupPosts: List<RecommendedGroupPost>)

    @Query("DELETE FROM recommended_group_posts WHERE group_id IN (:groupIds)")
    fun deleteRecommendedGroupPostByGroupIds(groupIds: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostTagCrossRefs(postTagCrossRefs: List<PostTagCrossRef>)

    @Query("DELETE FROM posts_tags WHERE post_id =:postId")
    fun deletePostTagCrossRefsByPostId(postId: String)

    @Query("DELETE FROM posts_tags WHERE post_id IN (:postIds)")
    fun deletePostTagCrossRefsByPostIds(postIds: List<String>)
}