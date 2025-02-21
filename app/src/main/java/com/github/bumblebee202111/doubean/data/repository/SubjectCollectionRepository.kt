package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.data.paging.SubjectCollectionItemPagingSource
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toSubjectCollection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCollectionRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getSubjectCollection(id: String) = suspendRunCatching {
        apiService.getSubjectCollection(id).toSubjectCollection()
    }

    fun getSubjectCollectionItemsPagingData(collectionId: String) = Pager(
        config = PagingConfig(
            pageSize = SUBJECT_COLLECTION_PAGE_SIZE,
            initialLoadSize = SUBJECT_COLLECTION_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SubjectCollectionItemPagingSource(
                backend = apiService,
                collectionId = collectionId,
            )
        }
    ).flow

    companion object {
        private const val SUBJECT_COLLECTION_PAGE_SIZE = 20
    }
}