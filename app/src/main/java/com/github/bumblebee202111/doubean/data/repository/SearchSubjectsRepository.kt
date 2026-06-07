package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectSearchResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectsSearchType
import com.github.bumblebee202111.doubean.model.subjects.toApiSubjectsSearchType
import com.github.bumblebee202111.doubean.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.network.model.search.toSubjectSearchResult
import com.github.bumblebee202111.doubean.network.util.makeApiCall
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