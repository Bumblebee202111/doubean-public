package com.github.bumblebee202111.doubean.data.db.model

import com.github.bumblebee202111.doubean.model.TopicSortBy
import java.util.Calendar

sealed interface FavoriteEntity {

    val groupId: String

    val favoriteDate: Calendar

    val enablePostNotifications: Boolean

    val allowDuplicateNotifications: Boolean

    val sortRecommendedPostsBy: TopicSortBy

    val feedRequestPostCountLimit: Int

    val lastNotifiedTimeMillis: Long

}