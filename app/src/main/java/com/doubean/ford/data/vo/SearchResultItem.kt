package com.doubean.ford.data.vo

import com.google.gson.annotations.SerializedName

class SearchResultItem(
    @SerializedName("target")
    val group: GroupItem
) : Item() {
    override val id: String
        get() = group.id
}