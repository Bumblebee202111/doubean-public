package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkTvDetail
import com.github.bumblebee202111.doubean.network.model.toPhotoList
import com.github.bumblebee202111.doubean.network.model.toTvDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getTv(tvId: String) =
        suspendRunCatching {
            apiService.getTv(tvId)
        }.map(NetworkTvDetail::toTvDetail)

    suspend fun getPhotos(tvId: String) = suspendRunCatching {
        apiService.getTvPhotos(tvId).toPhotoList()
    }
}