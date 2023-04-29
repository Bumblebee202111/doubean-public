package com.doubean.ford.api

import com.doubean.ford.data.vo.PostComment
import com.google.gson.annotations.SerializedName

class PostCommentsResponse(start: Int, count: Int, total: Int, items: List<PostComment>) :
    ListResponse<PostComment>(start, count, total, items) {
    @SerializedName("popular_comments")
    val topComments: List<PostComment>? = null
    val topCommentIds: List<String>
        get() {
            val comments: MutableList<String> = ArrayList()
            for (item in topComments!!) {
                comments.add(item.id)
            }
            return comments
        }
}