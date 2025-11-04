package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.common.toUserStatuses
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val service: ApiService,
) {
    suspend fun getFollowing() = makeApiCall(
        apiCall = {
            service.getTimeline()
        },
        mapSuccess = {
            it.toUserStatuses()
        }
    )
}