package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.SearchResultSubjectItem
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import com.github.bumblebee202111.doubean.model.toApiSubjectsSearchType
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toSearchResultSubjectItems
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchSubjectsRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun searchSubjects(
        query: String,
        type: SubjectsSearchType,
    ): Result<List<SearchResultSubjectItem>> {
        return suspendRunCatching {
            apiService.searchSubjects(query, type.toApiSubjectsSearchType())
        }.map { it.subjects.toSearchResultSubjectItems() }
    }
}