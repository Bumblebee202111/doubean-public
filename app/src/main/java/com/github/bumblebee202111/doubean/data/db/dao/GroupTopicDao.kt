package com.github.bumblebee202111.doubean.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostDetail
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItem
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostItemWithGroup
import com.github.bumblebee202111.doubean.data.db.model.PostDetailPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PostEntity
import com.github.bumblebee202111.doubean.data.db.model.PostItemPartialEntity
import com.github.bumblebee202111.doubean.data.db.model.PostTagCrossRef
import com.github.bumblebee202111.doubean.data.db.model.RecommendedGroupItemPostPartialEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface GroupTopicDao {
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

    @Insert(entity = PostEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostDetail(post: PostDetailPartialEntity)

    @Upsert(entity = PostEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertPosts(posts: List<PostItemPartialEntity>)

    @Upsert(entity = PostEntity::class)
    @RewriteQueriesToDropUnusedColumns
    suspend fun upsertRecommendedGroupItemPosts(posts: List<RecommendedGroupItemPostPartialEntity>)

    @Transaction
    @Query("SELECT * FROM posts WHERE id=:postId")
    fun loadPost(postId: String): Flow<PopulatedPostDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostTagCrossRefs(postTagCrossRefs: List<PostTagCrossRef>)

    @Query("DELETE FROM posts_tags WHERE post_id =:postId")
    suspend fun deletePostTagCrossRefsByPostId(postId: String)

    @Query("DELETE FROM posts_tags WHERE post_id IN (:postIds)")
    suspend fun deletePostTagCrossRefsByPostIds(postIds: List<String>)
}