package com.github.bumblebee202111.doubean.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.github.bumblebee202111.doubean.data.db.model.GroupTopicPopularCommentEntity
import com.github.bumblebee202111.doubean.data.db.model.PopulatedPostComment
import com.github.bumblebee202111.doubean.data.db.model.PostCommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupTopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(comments: List<GroupTopicPopularCommentEntity>)

    @Query("DELETE FROM group_topic_popular_comments WHERE topic_id = :topicId")
    fun deleteCommentsByTopicId(topicId: String)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM " +
                "group_topic_popular_comments LEFT OUTER JOIN post_comments " +
                "ON group_topic_popular_comments.comment_id == post_comments.id " +
                "WHERE topic_id = :topicId ORDER BY position"
    )
    fun observeCommentsByTopicId(topicId: String): Flow<List<PopulatedPostComment>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<PostCommentEntity>)

    @Transaction
    @Query("SELECT * FROM post_comments WHERE post_id=:postId ORDER BY id")
    fun getCommentsPagingSource(postId: String): PagingSource<Int, PopulatedPostComment>

    @Query("DELETE FROM post_comments WHERE post_id=:postId")
    fun deleteGroupPostComments(postId: String)
}