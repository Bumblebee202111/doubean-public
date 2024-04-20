package com.github.bumblebee202111.doubean.data.db.model

import com.github.bumblebee202111.doubean.model.PostSortBy
import java.util.Calendar

sealed interface FollowableEntity {

    val groupId: String

    val followDate: Calendar

    val enablePostNotifications: Boolean

    val allowDuplicateNotifications: Boolean

    val sortRecommendedPostsBy: PostSortBy

    val feedRequestPostCountLimit: Int

    val lastNotifiedTimeMillis: Long

}