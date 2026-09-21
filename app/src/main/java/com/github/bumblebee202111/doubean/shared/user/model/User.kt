package com.github.bumblebee202111.doubean.shared.user.model

data class User(
    val id: String,
    val uid: String,
    val name: String,
    val avatar: String,
    val uri: String,
    val alt: String,
) {
    val url get() = alt
}