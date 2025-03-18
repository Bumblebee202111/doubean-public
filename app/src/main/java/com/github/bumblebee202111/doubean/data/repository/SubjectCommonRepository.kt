package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.SubjectModule
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.safeApiCall
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectModules
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.toSubjectModules
import com.github.bumblebee202111.doubean.network.model.toSubjectReviewList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCommonRepository @Inject constructor(private val service: ApiService) {
    suspend fun getSubjectReviews(subjectType: SubjectType, subjectId: String) =
        suspendRunCatching {
            service.getSubjectReviews(
                subjectType = subjectType.toNetworkSubjectType().value,
                subjectId = subjectId
            ).toSubjectReviewList()
        }

    suspend fun getSubjectModules(subjectType: SubjectType): AppResult<List<SubjectModule>> =
        safeApiCall(
            apiCall = {
                service.getSubjectModules(subjectType = subjectType.toNetworkSubjectType().value)
            },
            mapSuccess = NetworkSubjectModules::toSubjectModules
        )
}