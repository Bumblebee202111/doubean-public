package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkTvWithInterest
import com.github.bumblebee202111.doubean.network.model.asTvWithInterest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getTv(tvId: String) =
        suspendRunCatching {
            apiService.getTv(tvId)
        }.map(NetworkTvWithInterest::asTvWithInterest)

}