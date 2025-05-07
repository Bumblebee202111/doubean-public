package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectModules
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectReviewList
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.toSubjectModules
import com.github.bumblebee202111.doubean.network.model.toSubjectReviewList
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectCommonRepository @Inject constructor(private val service: ApiService) {
    suspend fun getSubjectReviews(
        subjectType: SubjectType,
        subjectId: String,
    ): AppResult<SubjectReviewList> =
        makeApiCall(
            apiCall = {
                service.getSubjectReviews(
                    subjectType = subjectType.toNetworkSubjectType().value,
                    subjectId = subjectId
                )
            },
            mapSuccess = NetworkSubjectReviewList::toSubjectReviewList
        )

    suspend fun getSubjectModules(subjectType: SubjectType): AppResult<List<SubjectModule>> =
        makeApiCall(
            apiCall = {
                service.getSubjectModules(subjectType = subjectType.toNetworkSubjectType().value)
            },
            mapSuccess = NetworkSubjectModules::toSubjectModules
        )
}