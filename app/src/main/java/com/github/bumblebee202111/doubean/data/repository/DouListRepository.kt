package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.doulists.DouList
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.common.toDouListPosts
import com.github.bumblebee202111.doubean.network.model.doulists.toDouList
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DouListRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getDouList(douListId: String): AppResult<DouList> = makeApiCall(
        apiCall = {
            apiService.getDouList(douListId)
        },
        mapSuccess = {
            it.toDouList()
        }
    )

    suspend fun createDouList(title: String): AppResult<DouList> = makeApiCall(
        apiCall = {
            apiService.createDouList(title)
        },
        mapSuccess = {
            it.toDouList()
        }
    )

    suspend fun updateDouList(
        douListId: String,
        title: String,
        desc: String,
        isPrivate: Boolean,
        tags: String? = null,
    ): AppResult<DouList> =
        makeApiCall(
            apiCall = {
                apiService.updateDouList(douListId, title, desc, isPrivate, tags)
            },
            mapSuccess = {
                it.toDouList()
            }
        )

    suspend fun getDouListPosts(douListId: String): AppResult<List<DouListPostItem>> = makeApiCall(
        apiCall = {
            apiService.getDouListPosts(douListId)
        },
        mapSuccess = {
            it.toDouListPosts()
        }
    )
}