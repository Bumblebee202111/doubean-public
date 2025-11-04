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
import com.github.bumblebee202111.doubean.network.ApiService
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectInterest
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectInterestWithSubject
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectInterestWithSubjectList
import com.github.bumblebee202111.doubean.network.model.NetworkUserProfileSubjects
import com.github.bumblebee202111.doubean.network.model.toMySubjects
import com.github.bumblebee202111.doubean.network.model.toNetworkSubjectType
import com.github.bumblebee202111.doubean.network.model.toSubjectInterest
import com.github.bumblebee202111.doubean.network.model.toSubjectInterestWithUserList
import com.github.bumblebee202111.doubean.network.model.toSubjectWithInterest
import com.github.bumblebee202111.doubean.network.model.toSubjectWithInterestList
import com.github.bumblebee202111.doubean.network.util.makeApiCall
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSubjectRepository @Inject constructor(private val apiService: ApiService) {


    suspend fun getUserSubjects(userId: String): AppResult<List<MySubject>> = makeApiCall(
        apiCall = { apiService.getUserProfileSubjects(userId) },
        mapSuccess = NetworkUserProfileSubjects::toMySubjects
    )

    suspend fun getUserInterests(
        userId: String,
        subjectType: SubjectType,
    ): AppResult<List<SubjectWithInterest<Subject>>> = makeApiCall(
        apiCall = {
            apiService.getUserSubjectInterests(
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
                    backend = apiService,
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
    ): AppResult<SubjectWithInterest<Subject>> = makeApiCall(
        apiCall = {
            apiService.addSubjectToInterests(
                type = type.toNetworkSubjectType().value,
                id = id,
                newStatus = newStatus.toNetworkStatus().value
            )
        },
        mapSuccess = NetworkSubjectInterestWithSubject::toSubjectWithInterest
    )

    suspend fun unmarkSubject(type: SubjectType, id: String): AppResult<SubjectInterest> =
        makeApiCall(
            apiCall = {
                apiService.unmarkSubject(
                    type = type.toNetworkSubjectType().value,
                    id = id
                )
            },
            mapSuccess = NetworkSubjectInterest::toSubjectInterest
        )

    suspend fun getSubjectDoneFollowingHotInterests(
        type: SubjectType,
        id: String,
    ): AppResult<SubjectInterestWithUserList> {
        return makeApiCall(
            apiCall = {
                apiService.getSubjectDoneFollowingHotInterests(
                    type = type.toNetworkSubjectType().value,
                    id = id
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