package com.github.bumblebee202111.doubean.data.userdoulist

import com.github.bumblebee202111.doubean.core.network.api.DouListApiService
import com.github.bumblebee202111.doubean.core.network.util.makeApiCall
import com.github.bumblebee202111.doubean.data.doulist.toDouListPosts
import com.github.bumblebee202111.doubean.data.doulist.toDouLists
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.doulist.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulist.DouLists
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDouListRepository @Inject constructor(private val apiService: DouListApiService) {
    suspend fun getUserOwnedDouLists(userId: String, publicOnly: Boolean): AppResult<DouLists> =
        makeApiCall(
            apiCall = {
                apiService.getUserOwnedDouLists(userId, if (publicOnly) 1 else 0)
            },
            mapSuccess = {
                it.toDouLists()
            }
        )

    suspend fun getUserDouListPosts(userId: String): AppResult<List<DouListPostItem>> =
        makeApiCall(
            apiCall = {
                apiService.getUserDouListPosts(userId)
            },
            mapSuccess = {
                it.toDouListPosts()
            }
        )
}