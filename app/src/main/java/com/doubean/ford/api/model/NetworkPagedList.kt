package com.doubean.ford.api.model

interface NetworkPagedList<ItemType : Any> {
    val start: Int
    val count: Int
    val total: Int
    val items: List<ItemType>
    val nextPageStart: Int?
        get() {
            val next = start + items.size + 1
            return if (next > total) null else next
        }
}