package com.doubean.ford.data.db.model

import com.doubean.ford.model.PostSortBy
import java.util.*

sealed interface FollowableEntity {

    val groupId: String

    val followDate: Calendar

    val enablePostNotifications: Boolean

    val allowDuplicateNotifications: Boolean

    val sortRecommendedPostsBy: PostSortBy

    val feedRequestPostCountLimit: Int

    val lastNotifiedTimeMillis: Long

}