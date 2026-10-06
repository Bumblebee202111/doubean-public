package com.github.bumblebee202111.doubean.data.model

interface BaseFeedableItem {
    val id: String
    val title: String
    val uri: String
    val alt: String
    val type: String
    val sharingUrl: String
    val coverUrl: String
}