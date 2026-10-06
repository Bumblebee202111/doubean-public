package com.github.bumblebee202111.doubean.data.doulist

import com.github.bumblebee202111.doubean.core.network.api.DouListApiService
import com.github.bumblebee202111.doubean.core.network.util.makeApiCall
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.doulist.DouList
import com.github.bumblebee202111.doubean.model.doulist.DouListPostItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DouListRepository @Inject constructor(private val apiService: DouListApiService) {
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