package com.doubean.ford.model

import java.time.LocalDateTime

class PostComments(val topComments: List<PostComment>, val allComments: List<PostComment>)

data class PostComment(
    val id: String,

    val author: User,

    val photos: List<SizedPhoto>?,

    val text: String? = null,

    val created: LocalDateTime?,

    val voteCount: Int,

    val ipLocation: String,

    val repliedTo: RepliedToPostComment? = null,

    )

data class RepliedToPostComment(
    val id: String,

    val author: User,

    val photos: List<SizedPhoto>?,

    val text: String? = null,

    val created: LocalDateTime?,

    val voteCount: Int,

    val ipLocation: String,
)