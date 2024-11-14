package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.MySubject
import com.github.bumblebee202111.doubean.model.Subject
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.toNetworkStatus
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectInterest
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import com.github.bumblebee202111.doubean.network.model.toExternalModel
import com.github.bumblebee202111.doubean.network.model.toMySubjects
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSubjectRepository @Inject constructor(private val apiService: ApiService) {

    
    suspend fun getUserSubjects(userId: String): Result<List<MySubject>> = suspendRunCatching {
        apiService.getUserProfileSubjects(userId).toMySubjects()
    }

    suspend fun getUserInterests(userId: String, subjectType: SubjectType) = suspendRunCatching {
        apiService.getUserSubjectInterests(
            userId = userId,
            type = subjectType.toNetworkSubjectType().value
        ).toExternalModel()
    }

    suspend fun <T : Subject> addSubjectToInterests(
        subject: T,
        newStatus: SubjectInterest.Status,
    ): Result<SubjectWithInterest> {
        return suspendRunCatching {
            apiService.addSubjectToInterests(
                type = subject.type.toNetworkSubjectType().value,
                id = subject.id,
                newStatus = newStatus.toNetworkStatus().value
            )
                .asExternalModel()
        }
    }

    suspend fun unmarkSubject(subject: Subject): Result<NetworkSubjectInterest> {
        return suspendRunCatching {
            apiService.unmarkSubject(
                type = subject.type.toNetworkSubjectType().value,
                id = subject.id
            )
        }
    }
}