package com.doubean.ford.api

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents a recommendation of groups response from Douban.
 */
class RecommendedGroupsResponse(
    @SerializedName("groups")
    val items: List<RecommendedGroupResponseItem>,
    val title: String,
    val count:Int)