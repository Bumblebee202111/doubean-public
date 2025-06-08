package com.github.bumblebee202111.doubean.model.doulists

data class ItemDouLists(
    val count: Int,
    val start: Int,
    val total: Int,
    val douLists: List<ItemDouList>,
)