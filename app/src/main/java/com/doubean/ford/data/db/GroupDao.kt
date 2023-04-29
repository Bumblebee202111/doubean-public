package com.doubean.ford.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.doubean.ford.data.vo.*

/**
 * The Data Access Object for the [Group] class.
 */
@Dao
interface GroupDao {
    @Query(value = "SELECT * FROM groups WHERE id = :groupId")
    @RewriteQueriesToDropUnusedColumns
    fun loadDetail(groupId: String): LiveData<GroupDetail>

    @Upsert(entity = Group::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertDetail(group: GroupDetail)

    @Update(entity = Group::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertGroupBrief(group: GroupBrief)

    @Query("SELECT * FROM groups")
    @RewriteQueriesToDropUnusedColumns
    fun getGroups(): LiveData<List<GroupItem>>

    @Upsert(entity = Group::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertGroups(groupList: List<GroupItem>)

    @Query("SELECT * FROM group_search_results WHERE `query` = :query")
    fun search(query: String): LiveData<GroupSearchResult?>

    @Query("SELECT * FROM group_search_results WHERE `query` = :query")
    fun findSearchResult(query: String): GroupSearchResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupSearchResult(groupSearchResult: GroupSearchResult?)

    @Query("SELECT * FROM groups WHERE id IN (:groupIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupsById(groupIds: List<String>): LiveData<List<GroupItem>>

    fun loadOrderedGroups(
        groupIds: List<String>,
        c: Comparator<GroupItem>? = null
    ): LiveData<List<GroupItem>> {
        return Transformations.map(loadGroupsById(groupIds)) { groups ->
            if (c == null) {
                groups.sortedWith(compareBy { o: GroupItem -> groupIds.indexOf(o.id) })
            } else groups.sortedWith(c)
            groups
        }
    }

    /* Posts-related operations start */
    @Query("SELECT * FROM posts WHERE groupId = :groupId ORDER BY created DESC")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupPosts(groupId: String): LiveData<List<PostItem>>

    @Query("SELECT * FROM posts WHERE groupId = :groupId AND tagId=:tagId ORDER BY created DESC")
    @RewriteQueriesToDropUnusedColumns
    fun loadGroupTagPosts(groupId: String, tagId: String): LiveData<List<PostItem>>

    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    @RewriteQueriesToDropUnusedColumns
    fun loadPostsById(postIds: List<String>): LiveData<List<PostItem>>
    fun loadPosts(postIds: List<String>): LiveData<List<PostItem>> {
        return Transformations.map(loadPostsById(postIds)) { posts ->
            posts.sortedWith(compareBy { o: PostItem -> postIds.indexOf(o.id) })
        }
    }

    @Query("SELECT * FROM GroupPostsResult WHERE groupId = :groupId AND sortBy = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun getGroupPosts(groupId: String, sortBy: PostSortBy): LiveData<GroupPostsResult?>

    @Query("SELECT * FROM GroupPostsResult WHERE groupId = :groupId AND sortBy = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun findGroupPosts(groupId: String, sortBy: PostSortBy): GroupPostsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupPostsResult(groupPostsResult: GroupPostsResult?)

    @Query("SELECT * FROM group_tag_posts_results WHERE groupId = :groupId AND tagId=:tagId AND sortBy = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun getGroupTagPosts(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy
    ): LiveData<GroupTagPostsResult?>

    @Query("SELECT * FROM group_tag_posts_results WHERE groupId = :groupId AND tagId=:tagId AND sortBy = :sortBy")
    @RewriteQueriesToDropUnusedColumns
    fun findGroupTagPosts(
        groupId: String,
        tagId: String,
        sortBy: PostSortBy
    ): GroupTagPostsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupTagPostsResult(groupTagPostsResult: GroupTagPostsResult?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post)

    @Upsert(entity = Post::class)
    @RewriteQueriesToDropUnusedColumns
    fun upsertPosts(List: List<PostItem>)

    @Query("SELECT * FROM posts WHERE id=:postId")
    fun loadPost(postId: String): LiveData<Post>

    @Query("SELECT * FROM post_top_comments WHERE postId = :postId")
    fun loadPostTopComments(postId: String): LiveData<PostTopComments?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostTopComments(topComments: PostTopComments?)

    fun loadOrderedComments(
        commentIds: List<String>,
        c: Comparator<PostComment>? = null
    ): LiveData<List<PostComment>>? {
        return Transformations.map(
            loadCommentsById(commentIds)
        ) { comments ->
            if (c != null) comments.sortedWith(c) else comments.sortedWith(
                compareBy { o: PostComment -> commentIds.indexOf(o.id) })
            comments
        }
    }

    @Query("SELECT * FROM post_comments WHERE id IN (:commentIds)")
    fun loadCommentsById(commentIds: List<String>): LiveData<List<PostComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostComments(comments: List<PostComment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPostCommentsResult(postCommentsResult: PostCommentsResult?)

    @Query("SELECT * FROM post_comments_results WHERE postId=:postId")
    fun getPostComments(postId: String): LiveData<PostCommentsResult?>

    @Query("SELECT * FROM post_comments_results WHERE postId=:postId")
    fun findPostComments(postId: String): PostCommentsResult?

    @Query("SELECT * FROM recommended_groups_result WHERE type=:type")
    fun getRecommendedGroups(type: GroupRecommendationType): LiveData<RecommendedGroupsResult?>

    @Upsert(entity = RecommendedGroupResult::class)
    fun upsertRecommendedGroups(recommendedGroupResults: List<RecommendedGroupResult?>): List<Long>

    @Transaction
    @Query("SELECT * FROM recommended_groups_results WHERE id IN (:ids)")
    @RewriteQueriesToDropUnusedColumns
    fun loadRecommendedGroupsByIds(ids: List<Long>): LiveData<List<RecommendedGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecommendedGroupsResult(recommendedGroupsResult: RecommendedGroupsResult?)
}