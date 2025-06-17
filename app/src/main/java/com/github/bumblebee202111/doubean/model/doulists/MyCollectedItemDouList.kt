package com.github.bumblebee202111.doubean.model.doulists

import com.github.bumblebee202111.doubean.model.fangorns.User

data class MyCollectedItemDouList(
    val id: String,
    val title: String,
    val uri: String,
    val alt: String,
    val type: String,
    val sharingUrl: String,
    val owner: User,
    val category: String,
    
    
    val doulistType: String?,
    val listType: String?,
)