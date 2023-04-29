package com.doubean.ford.api

import com.doubean.ford.data.vo.Item
import com.google.gson.annotations.SerializedName

open class ListResponse<ItemType : Item>(
    var start: Int, var count: Int, var total: Int, @field:SerializedName(
        value = "items",
        alternate = ["topics", "comments", "groups"]
    ) var items: List<ItemType>
) {

    val ids: List<String>
        get() {
            val ids: MutableList<String> = ArrayList()
            for (item in items) {
                ids.add(item.id)
            }
            return ids
        }
    val nextPageStart: Int?
        get() {
            val next = start + items.size + 1
            return if (next > total) null else next
        }
}