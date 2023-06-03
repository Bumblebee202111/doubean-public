package com.doubean.ford.data.db.model

import androidx.room.*
import com.doubean.ford.data.db.Converters

@TypeConverters(Converters::class)
interface PagedListResult {
    val next: Int?
    val totalCount: Int
    val ids: List<String>
}

