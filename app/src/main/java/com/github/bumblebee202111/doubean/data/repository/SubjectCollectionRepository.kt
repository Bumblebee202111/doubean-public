package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toSubjectCollection
import com.github.bumblebee202111.doubean.network.model.toSubjectWithRankAndInterest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCollectionRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getSubjectCollection(id: String) = suspendRunCatching {
        apiService.getSubjectCollection(id).toSubjectCollection()
    }

    suspend fun getSubjectCollectionItems(collectionId: String) = suspendRunCatching {
        apiService.getSubjectCollectionItems(
            collectionId = collectionId,
            start = 0,
            count = SUBJECT_COLLECTION_PAGE_SIZE
        )
    }.map {
        it.items.mapIndexed { index, item -> item.toSubjectWithRankAndInterest(index + 1) }
    }

    companion object {
        private const val SUBJECT_COLLECTION_PAGE_SIZE = 20
    }
}