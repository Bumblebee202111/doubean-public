package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "favorite_groups",
)
data class FavoriteGroupEntity(
    @PrimaryKey
    @ColumnInfo("group_id")
    override val groupId: String,

    @ColumnInfo("favorite_date")
    override val favoriteDate: Calendar = Calendar.getInstance(),
) : FavoriteEntity