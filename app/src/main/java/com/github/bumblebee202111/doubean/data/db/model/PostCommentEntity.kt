package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.github.bumblebee202111.doubean.data.db.Converters
import com.github.bumblebee202111.doubean.model.SizedPhoto
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