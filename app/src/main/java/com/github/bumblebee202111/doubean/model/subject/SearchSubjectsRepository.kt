package com.github.bumblebee202111.doubean.model.subject

import com.github.bumblebee202111.doubean.core.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.core.network.util.makeApiCall
import com.github.bumblebee202111.doubean.data.subject.toApiSubjectsSearchType
import com.github.bumblebee202111.doubean.data.subject.toSubjectSearchResult
import com.github.bumblebee202111.doubean.model.AppResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchSubjectsRepository @Inject constructor(private val apiService: SubjectApiService) {
    suspend fun searchSubjects(
        query: String,
        type: SubjectsSearchType? = null,
    ): AppResult<SubjectSearchResult> {
        return makeApiCall(
            apiCall = {
                apiService.searchSubjects(q = query, type = type?.toApiSubjectsSearchType())
            },
            mapSuccess = {
                it.toSubjectSearchResult()
            }
        )
    }

}