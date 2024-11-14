package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkBookWithInterest
import com.github.bumblebee202111.doubean.network.model.NetworkMovieWithInterest
import com.github.bumblebee202111.doubean.network.model.asBookWithInterest
import com.github.bumblebee202111.doubean.network.model.asMovieWithInterest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCollectionRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getTop250MoviesCollection() = suspendRunCatching {
        apiService.getSubjectCollectionMovieTop250()
    }

    suspend fun getTop250MoviesCollectionItems() = suspendRunCatching {
        apiService.getSubjectCollectionMovieTop250Items(
            start = 0,
            count = SUBJECT_COLLECTION_PAGE_SIZE
        )
    }.map {
        it.items.map(NetworkMovieWithInterest::asMovieWithInterest)
    }

    suspend fun getTop250BooksCollection() = suspendRunCatching {
        apiService.getSubjectCollectionBookTop250()
    }

    suspend fun getTop250BooksCollectionItems() = suspendRunCatching {
        apiService.getSubjectCollectionBookTop250Items(
            start = 0,
            count = SUBJECT_COLLECTION_PAGE_SIZE
        )
    }.map {
        it.items.map(NetworkBookWithInterest::asBookWithInterest)
    }

    companion object {
        private const val SUBJECT_COLLECTION_PAGE_SIZE = 20
    }
}