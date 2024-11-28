package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkMovieWithInterest
import com.github.bumblebee202111.doubean.network.model.asMovieWithInterest
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
        }.map(NetworkMovieWithInterest::asMovieWithInterest)

    suspend fun getTop250MoviesCollection() =
        subjectCollectionRepository.getTop250MoviesCollection()

    suspend fun getTop250MoviesCollectionItems() =
        subjectCollectionRepository.getTop250MoviesCollectionItems()

}