package com.github.bumblebee202111.doubean.data.db.model

import androidx.room.TypeConverters
import com.github.bumblebee202111.doubean.data.db.Converters

@TypeConverters(Converters::class)
interface PagedListResult {
    val next: Int?
    val totalCount: Int
    val ids: List<String>
}

