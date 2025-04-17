package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectSearchResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectsSearchType
import com.github.bumblebee202111.doubean.model.subjects.toApiSubjectsSearchType
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toSubjectSearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchSubjectsRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun searchSubjects(
        query: String,
        type: SubjectsSearchType? = null,
    ): AppResult<SubjectSearchResult> {
        return safeApiCall(
            apiCall = {
                apiService.searchSubjects(q = query, type = type?.toApiSubjectsSearchType())
            },
            mapSuccess = {
                it.toSubjectSearchResult()
            }
        )
    }

}