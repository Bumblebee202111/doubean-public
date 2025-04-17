package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.bumblebee202111.doubean.model.User

@Entity("users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val uid: String,
    val name: String,
    val avatar: String,
)

fun UserEntity.asExternalModel() = User(
    id = id, uid = uid, name = name, avatar = avatar
)