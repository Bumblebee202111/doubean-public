package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulists.DouLists
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.common.toDouListPosts
import com.github.bumblebee202111.doubean.network.model.doulists.toDouLists
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDouListRepository @Inject constructor(private val apiService: ApiService) {
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