package com.github.bumblebee202111.doubean.data.repository

import com.github.bumblebee202111.doubean.coroutines.suspendRunCatching
import com.github.bumblebee202111.doubean.model.MySubject
import com.github.bumblebee202111.doubean.model.Subject
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.toNetworkStatus
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.asExternalModel
import com.github.bumblebee202111.doubean.network.model.toExternalModel
import com.github.bumblebee202111.doubean.network.model.toMySubjects
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.toSubjectInterest
import com.github.bumblebee202111.doubean.network.model.toSubjectInterestWithUserList
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
        type: SubjectType, id: String,
        newStatus: SubjectInterestStatus,
    ): Result<SubjectWithInterest<T>> {
        return suspendRunCatching {
            @Suppress("UNCHECKED_CAST")
            apiService.addSubjectToInterests(
                type = type.toNetworkSubjectType().value,
                id = id,
                newStatus = newStatus.toNetworkStatus().value
            )
                .asExternalModel() as SubjectWithInterest<T>
        }
    }

    suspend fun unmarkSubject(type: SubjectType, id: String): Result<SubjectInterest> {
        return suspendRunCatching {
            apiService.unmarkSubject(
                type = type.toNetworkSubjectType().value,
                id = id
            ).toSubjectInterest()
        }
    }

    suspend fun getSubjectDoneFollowingHotInterests(
        type: SubjectType,
        id: String,
    ): Result<SubjectInterestWithUserList> {
        return suspendRunCatching {
            apiService.getSubjectDoneFollowingHotInterests(
                type = type.toNetworkSubjectType().value,
                id = id
            )
                .toSubjectInterestWithUserList()
        }
    }
}