package com.doubean.ford.data.db.model

import androidx.room.*
import com.doubean.ford.data.db.Converters
import com.doubean.ford.model.SizedPhoto
import java.time.LocalDateTime

@Entity(tableName = "post_comments")
@TypeConverters(Converters::class)
data class PostCommentEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo("post_id")
    val postId: String,

    @ColumnInfo("author_id")
    val authorId: String,

    val photos: List<SizedPhoto>?,

    val text: String? = null,

    @ColumnInfo("create_time")
    val created: LocalDateTime?,

    @ColumnInfo("vote_count")
    val voteCount: Int,

    @ColumnInfo("ip_location")
    val ipLocation: String,

    @ColumnInfo("replied_to_id")
    val repliedToId: String? = null,
) 