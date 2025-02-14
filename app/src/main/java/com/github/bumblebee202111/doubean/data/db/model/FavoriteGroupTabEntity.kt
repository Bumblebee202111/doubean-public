package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "favorite_group_tabs",
)
data class FavoriteGroupTabEntity(
    @PrimaryKey
    @ColumnInfo("tab_id")
    val tabId: String,
    @ColumnInfo("group_id")
    override val groupId: String,
    @ColumnInfo("favorite_date")
    override val favoriteDate: Calendar = Calendar.getInstance(),
    ) : FavoriteEntity

