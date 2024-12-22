package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkMovieDetail
import com.github.bumblebee202111.doubean.network.model.toMovieDetail
import com.github.bumblebee202111.doubean.network.model.toPhotoList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: ApiService,
    private val subjectCollectionRepository: SubjectCollectionRepository,
) {

    suspend fun getMovie(movieId: String) =
        suspendRunCatching {
            apiService.getMovie(movieId)
        }.map(NetworkMovieDetail::toMovieDetail)

    suspend fun getPhotos(movieId: String) = suspendRunCatching {
        apiService.getMoviePhotos(movieId).toPhotoList()
    }


}