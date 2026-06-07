package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.network.model.fangorns.toPhotoList
import com.github.bumblebee202111.doubean.network.model.subject.toMovieDetail
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: SubjectApiService,
) {
    suspend fun getMovie(movieId: String) =
        makeApiCall(
            apiCall = {
                apiService.getMovie(movieId)
            },
            mapSuccess = {
                it.toMovieDetail()
            }
        )

    suspend fun getPhotos(movieId: String) = makeApiCall(
        apiCall = {
            apiService.getMoviePhotos(movieId)
        },
        mapSuccess = {
            it.toPhotoList()
        }
    )
}