package com.doubean.ford.api

import com.doubean.ford.data.vo.SearchResultItem

/**
 * Data class that represents a group search response from Douban.
 */
class GroupSearchResponse(
    start: Int,
    count: Int,
    total: Int,
    private val q: String,
    items: List<SearchResultItem>
) : ListResponse<SearchResultItem>(start, count, total, items) {
    val groupIds: List<String>
        get() {
            val groupIds: MutableList<String> = ArrayList()
            for (item in items) {
                groupIds.add(item.group.id)
            }
            return groupIds
        }
}