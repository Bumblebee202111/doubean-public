package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val uid: String,
    val name: String,
    val avatar: String,
    val uri: String,
    val url: String,
)

