package com.doubean.ford.api

import com.doubean.ford.data.vo.PostItem

class PostsResponse(start: Int, count: Int, total: Int, items: List<PostItem>) :
    ListResponse<PostItem>(start, count, total, items) {
}