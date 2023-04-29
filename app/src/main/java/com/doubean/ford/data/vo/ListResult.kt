package com.doubean.ford.data.vo

import androidx.room.*
import com.doubean.ford.data.db.Converters

@Entity
@TypeConverters(Converters::class)
interface ListResult{
    val next: Int?
    val totalCount:Int
    val ids:List<String>
}