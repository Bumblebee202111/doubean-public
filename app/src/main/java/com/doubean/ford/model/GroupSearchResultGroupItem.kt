package com.doubean.ford.model

import androidx.room.ColumnInfo
import java.time.LocalDateTime

/** Defines the UI layer model of an item of a group list
 * No need to define a separated GroupItem because a group model always comes along with user data in UI layer
 * */

data class GroupSearchResultGroupItem(

    val id: String,

    val name: String,

    @ColumnInfo("avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo("member_name")
    val memberName: String? = null,

    @ColumnInfo("member_count")
    val memberCount: Int? = null,

    @ColumnInfo("post_count")
    val postCount: Int? = null,

    @ColumnInfo("date_created")
    val dateCreated: LocalDateTime? = null,

    @ColumnInfo("short_description")
    val shortDescription: String? = null,

    @ColumnInfo("share_url")
    val shareUrl: String,

    val url: String,

    val uri: String,

    )