package com.github.bumblebee202111.doubean.data.db.model

import com.github.bumblebee202111.doubean.model.TopicSortBy
import java.util.Calendar

sealed interface FavoriteEntity {

    val groupId: String

    val favoriteDate: Calendar

    val enableTopicNotifications: Boolean

    val allowDuplicateNotifications: Boolean

    val sortRecommendedTopicsBy: TopicSortBy

    val feedRequestTopicCountLimit: Int

    val lastNotifiedTimeMillis: Long

}