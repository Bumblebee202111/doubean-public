package com.doubean.ford.api

import com.doubean.ford.data.vo.PostItem

class PostsResponse(start: Int, count: Int, total: Int, items: List<PostItem>) :
    ListResponse<PostItem>(start, count, total, items) {
    val postIds: List<String>
        get() {
            val postIds: MutableList<String> = ArrayList()
            for (item in items) {
                postIds.add(item.id)
            }
            return postIds
        }
}