package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkBookDetail
import com.github.bumblebee202111.doubean.network.model.toBookDetail
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getBook(bookId: String) =
        makeApiCall(
            apiCall = { apiService.getBook(bookId) },
            mapSuccess = NetworkBookDetail::toBookDetail
        )
}