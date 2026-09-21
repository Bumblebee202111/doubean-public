package com.github.bumblebee202111.doubean.shared.doulist.model

data class ItemDouLists(
    val count: Int,
    val start: Int,
    val total: Int,
    val douLists: List<ItemDouList>,
)