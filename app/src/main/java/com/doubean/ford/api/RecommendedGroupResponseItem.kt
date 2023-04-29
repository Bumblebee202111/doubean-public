package com.doubean.ford.api

import com.doubean.ford.data.vo.GroupItem
import com.doubean.ford.data.vo.PostItem
import com.google.gson.annotations.SerializedName

class RecommendedGroupResponseItem(
    val trend: Int,
    @SerializedName("topics")
    val posts: List<PostItem>,
    val group: GroupItem
)