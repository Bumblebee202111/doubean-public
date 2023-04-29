package com.doubean.ford.data.vo

import androidx.room.*
import com.doubean.ford.data.db.Converters
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Entity(tableName = "post_comments")
@TypeConverters(Converters::class)
data class PostComment  (
    @PrimaryKey
    override val id: String,

    val author: User,

    val photos: List<SizedPhoto>? = null,

    val text: String? = null,

    @SerializedName("create_time")
    val created: LocalDateTime? = null,

    @SerializedName("vote_count")
    val voteCount: Int = 0,

    @SerializedName("ref_comment")
    val repliedTo: PostComment? = null

):Item(){
    @ColumnInfo(name = "post_id")
    lateinit var postId: String
}