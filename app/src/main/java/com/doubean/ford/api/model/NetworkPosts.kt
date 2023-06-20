package com.doubean.ford.api.model

import com.google.gson.annotations.SerializedName

class NetworkPosts(
    override val start: Int,
    override val count: Int,
    override val total: Int,
    @SerializedName("topics")
    override val items: List<NetworkPostItem>,
) : NetworkPagedList<NetworkPostItem>