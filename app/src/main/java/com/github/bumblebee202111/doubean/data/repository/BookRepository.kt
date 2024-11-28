package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkBookWithInterest
import com.github.bumblebee202111.doubean.network.model.asBookWithInterest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val apiService: ApiService,
    private val subjectCollectionRepository: SubjectCollectionRepository,
) {

    suspend fun getBook(bookId: String) =
        suspendRunCatching {
            apiService.getBook(bookId)
        }.map(NetworkBookWithInterest::asBookWithInterest)

    suspend fun getTop250BooksCollection() =
        subjectCollectionRepository.getTop250BooksCollection()

    suspend fun getTop250BooksCollectionItems() =
        subjectCollectionRepository.getTop250BooksCollectionItems()

}