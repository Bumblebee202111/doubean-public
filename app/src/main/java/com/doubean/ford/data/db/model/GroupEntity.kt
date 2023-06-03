package com.doubean.ford.data.db.model

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "groups")
class GroupEntity(
    @PrimaryKey
    val id: String,
    val name: String,

    @ColumnInfo("member_count")
    val memberCount: Int? = null,

    @ColumnInfo("post_count")
    val postCount: Int? = null,

    @ColumnInfo("share_url")
    val shareUrl: String,

    val url: String,

    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime? = null,

    val uri: String?,

    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo("member_name")
    val memberName: String? = null,

    @ColumnInfo("short_description")
    val shortDescription: String? = null,

    val description: String? = null,

    val color: Int? = null,
)








