package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.network.model.fangorns.toPhotoList
import com.github.bumblebee202111.doubean.network.model.subject.NetworkTvDetail
import com.github.bumblebee202111.doubean.network.model.subject.toTvDetail
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepository @Inject constructor(
    private val apiService: SubjectApiService,
) {
    suspend fun getTv(tvId: String) =
        makeApiCall(
            apiCall = {
                apiService.getTv(tvId)
            },
            mapSuccess =
                NetworkTvDetail::toTvDetail
        )

    suspend fun getPhotos(tvId: String) = makeApiCall(
        apiCall = {
            apiService.getTvPhotos(tvId)
        },
        mapSuccess = {
            it.toPhotoList()
        }
    )
}