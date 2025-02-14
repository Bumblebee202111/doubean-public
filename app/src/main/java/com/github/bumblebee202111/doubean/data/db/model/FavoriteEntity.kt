package com.github.bumblebee202111.doubean.data.db.model

import java.util.Calendar

sealed interface FavoriteEntity {
    val groupId: String
    val favoriteDate: Calendar
}