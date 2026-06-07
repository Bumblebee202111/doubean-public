package com.github.bumblebee202111.doubean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.github.bumblebee202111.doubean.data.paging.UserSubjectInterestItemPagingSource
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.MySubject
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.subjects.toNetworkStatus
import com.github.bumblebee202111.doubean.network.api.SubjectApiService
import com.github.bumblebee202111.doubean.network.api.UserApiService
import com.github.bumblebee202111.doubean.network.model.NetworkUserProfileSubjects
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSubjectInterest
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSubjectInterestWithSubject
import com.github.bumblebee202111.doubean.network.model.subject.NetworkSubjectInterestWithSubjectList
import com.github.bumblebee202111.doubean.network.model.subject.toSubjectInterest
import com.github.bumblebee202111.doubean.network.model.subject.toSubjectInterestWithUserList
import com.github.bumblebee202111.doubean.network.model.subject.toSubjectWithInterest
import com.github.bumblebee202111.doubean.network.model.subject.toSubjectWithInterestList
import com.github.bumblebee202111.doubean.network.model.toMySubjects
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSubjectRepository @Inject constructor(
    private val subjectApiService: SubjectApiService,
    private val userApiService: UserApiService,
) {

    suspend fun getUserSubjects(userId: String): AppResult<List<MySubject>> = makeApiCall(
        apiCall = { userApiService.getUserProfileSubjects(userId) },
        mapSuccess = NetworkUserProfileSubjects::toMySubjects
    )

    suspend fun getUserInterests(
        userId: String,
        subjectType: SubjectType,
    ): AppResult<List<SubjectWithInterest<Subject>>> = makeApiCall(
        apiCall = {
            userApiService.getUserSubjectInterests(
                userId = userId,
                type = subjectType.toNetworkSubjectType().value,
                count = USER_SUBJECT_COUNT
            )
        },
        mapSuccess = NetworkSubjectInterestWithSubjectList::toSubjectWithInterestList
    )

    fun getUserInterestsPagingData(
        userId: String,
        subjectType: SubjectType,
        skipFirstPage: Boolean = true,
    ) =
        Pager(
            config = PagingConfig(
                pageSize = USER_SUBJECT_COUNT,
                initialLoadSize = USER_SUBJECT_COUNT,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserSubjectInterestItemPagingSource(
                    apiService = userApiService,
                    userId = userId,
                    subjectType = subjectType,
                    initialStart = if (skipFirstPage) USER_SUBJECT_COUNT else 0
                )
            }
        ).flow.map { pagingData -> pagingData.map(NetworkSubjectInterestWithSubject::toSubjectWithInterest) }


    suspend fun addSubjectToInterests(
        type: SubjectType,
        id: String,
        newStatus: SubjectInterestStatus,
        rating: Int? = null,
    ): AppResult<SubjectWithInterest<Subject>> = makeApiCall(
        apiCall = {
            subjectApiService.addSubjectToInterests(
                type = type.toNetworkSubjectType().value,
                id = id,
                newStatus = newStatus.toNetworkStatus().value,
                rating = rating
            )
        },
        mapSuccess = NetworkSubjectInterestWithSubject::toSubjectWithInterest
    )

    suspend fun unmarkSubject(type: SubjectType, id: String): AppResult<SubjectInterest> =
        makeApiCall(
            apiCall = {
                subjectApiService.unmarkSubject(
                    type = type.toNetworkSubjectType().value,
                    id = id
                )
            },
            mapSuccess = NetworkSubjectInterest::toSubjectInterest
        )

    suspend fun getSubjectFollowingHotInterests(
        type: SubjectType,
        id: String,
        status: SubjectInterestStatus,
    ): AppResult<SubjectInterestWithUserList> {
        return makeApiCall(
            apiCall = {
                subjectApiService.getSubjectFollowingHotInterests(
                    type = type.toNetworkSubjectType().value,
                    id = id,
                    status = status.toNetworkStatus().value
                )
            },
            mapSuccess = {
                it.toSubjectInterestWithUserList()
            }
        )
    }

    suspend fun getSubjectHotInterests(
        type: SubjectType,
        id: String,
        status: SubjectInterestStatus,
    ): AppResult<SubjectInterestWithUserList> {
        return makeApiCall(
            apiCall = {
                subjectApiService.getSubjectHotInterests(
                    type = type.toNetworkSubjectType().value,
                    id = id,
                    status = status.toNetworkStatus().value
                )
            },
            mapSuccess = {
                it.toSubjectInterestWithUserList()
            }
        )
    }

    private companion object {
        const val USER_SUBJECT_COUNT = 20
    }
}