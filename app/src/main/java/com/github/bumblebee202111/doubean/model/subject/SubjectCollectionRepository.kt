package com.github.bumblebee202111.doubean.model.subject

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.github.bumblebee202111.doubean.core.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.core.network.util.makeApiCall
import com.github.bumblebee202111.doubean.data.subject.toSubjectCollection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCollectionRepository @Inject constructor(private val apiService: SubjectApiService) {
    suspend fun getSubjectCollection(id: String) = makeApiCall(
        apiCall = { apiService.getSubjectCollection(id) },
        mapSuccess = {
            it.toSubjectCollection()
        }
    )

    fun getSubjectCollectionItemsPagingData(collectionId: String) = Pager(
        config = PagingConfig(
            pageSize = SUBJECT_COLLECTION_PAGE_SIZE,
            initialLoadSize = SUBJECT_COLLECTION_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SubjectCollectionItemPagingSource(
                apiService = apiService,
                collectionId = collectionId,
            )
        }
    ).flow

    companion object {
        private const val SUBJECT_COLLECTION_PAGE_SIZE = 20
    }
}