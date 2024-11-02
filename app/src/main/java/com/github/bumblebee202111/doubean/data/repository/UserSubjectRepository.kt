package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.MySubject
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.toExternalModel
import com.github.bumblebee202111.doubean.network.model.toMySubjects
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSubjectRepository @Inject constructor(private val apiService: ApiService) {

    /**
     * @return Books and movies
     */
    suspend fun getUserSubjects(userId: String): Result<List<MySubject>> = suspendRunCatching {
        apiService.getUserProfileSubjects(userId).toMySubjects()
    }

    suspend fun getUserInterests(userId: String, subjectType: SubjectType) = suspendRunCatching {
        apiService.getUserSubjectInterests(
            userId = userId,
            type = subjectType.toNetworkSubjectType().value
        ).toExternalModel()
    }
}