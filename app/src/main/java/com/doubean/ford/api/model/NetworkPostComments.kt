package com.doubean.ford.api.model

import androidx.room.PrimaryKey
import com.doubean.ford.data.db.model.PostCommentEntity
import com.doubean.ford.model.SizedPhoto
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class NetworkPostComments(
    override val start: Int,
    override val count: Int,
    override val total: Int,
    @SerializedName("comments")
    val allComments: List<NetworkPostComment>,
    @SerializedName("popular_comments")
    val topComments: List<NetworkPostComment>,
) : NetworkPagedList<NetworkPostComment> {
    override val items: List<NetworkPostComment>
        get() = allComments

}

data class NetworkPostComment(
    @PrimaryKey
    val id: String,

    val author: NetworkUser,

    val photos: List<SizedPhoto>?,

    val text: String? = null,

    @SerializedName("create_time")
    val created: LocalDateTime?,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("ref_comment")
    val repliedTo: NetworkRepliedToPostComment?,

    @SerializedName("ip_location")
    val ipLocation: String,

    )

data class NetworkRepliedToPostComment(
    @PrimaryKey
    val id: String,

    val author: NetworkUser,

    val photos: List<SizedPhoto>?,

    val text: String? = null,

    @SerializedName("create_time")
    val created: LocalDateTime?,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("ip_location")
    val ipLocation: String,
)

fun NetworkPostComment.asEntity(postId: String) = PostCommentEntity(
    id = id,
    postId = postId,
    authorId = author.id,
    photos = photos,
    text = text,
    created = created,
    voteCount = voteCount,
    repliedToId = repliedTo?.id,
    ipLocation = ipLocation
)

fun NetworkRepliedToPostComment.asEntity(postId: String) = PostCommentEntity(
    id = id,
    postId = postId,
    authorId = author.id,
    photos = photos,
    text = text,
    created = created,
    voteCount = voteCount,
    ipLocation = ipLocation
)