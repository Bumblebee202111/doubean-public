package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.common.CollectType
import com.github.bumblebee202111.doubean.model.common.toRequestPath
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.doulists.toDouListItem
import com.github.bumblebee202111.doubean.network.model.doulists.toItemDouLists
import com.github.bumblebee202111.doubean.network.model.structure.toCollectionItem
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemDouListRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getItemAvailableDouLists(type: CollectType, id: String) = makeApiCall(
        apiCall = { apiService.getItemAvailableDouLists(type = type.toRequestPath(), id = id) },
        mapSuccess = { it.toItemDouLists() }
    )

    suspend fun collectItem(type: CollectType, id: String, douListId: String) = makeApiCall(
        apiCall = {
            apiService.collectItem(
                type = type.toRequestPath(),
                id = id,
                douListId = douListId
            )
        },
        mapSuccess = { it.toDouListItem() }
    )

    suspend fun uncollectItem(type: CollectType, id: String, douListId: String) = makeApiCall(
        apiCall = {
            apiService.uncollectItem(
                type = type.toRequestPath(),
                id = id,
                douListId = douListId
            )
        },
        mapSuccess = { it.toCollectionItem() }
    )
}