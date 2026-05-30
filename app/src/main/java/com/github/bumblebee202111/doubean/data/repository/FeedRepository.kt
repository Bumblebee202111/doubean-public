package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.network.api.FeedApiService
import com.github.bumblebee202111.doubean.network.model.common.toUserStatuses
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val apiService: FeedApiService,
) {
    suspend fun getFollowing() = makeApiCall(
        apiCall = {
            apiService.getTimeline()
        },
        mapSuccess = {
            it.toUserStatuses()
        }
    )
}