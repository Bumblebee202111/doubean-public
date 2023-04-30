package com.doubean.ford.api

import com.doubean.ford.data.vo.Item
import com.google.gson.annotations.SerializedName

open class ListResponse<ItemType : Item>(
    val start: Int, val count: Int, val total: Int, @field:SerializedName(
        value = "items",
        alternate = ["topics", "comments", "groups"]
    ) val items: List<ItemType>
) {

    val nextPageStart: Int?
        get() {
            val next = start + items.size + 1
            return if (next > total) null else next
        }
}