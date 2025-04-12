@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.InterestsRoute
import com.github.bumblebee202111.doubean.model.subjects.MySubjectStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    private val route = savedStateHandle.toRoute<InterestsRoute>()
    private val userId = route.userId
    private val subjectType = route.subjectType
    private val subjectResult = flow {
        emit(userSubjectRepository.getUserSubjects(userId).map { subjects ->
            subjects.first { it.type == subjectType }
        })
    }.stateInUi()

    private val interests = MutableStateFlow<List<SubjectWithInterest<*>>>(emptyList())

    private val interestsResult =
        flow {
            emit(userSubjectRepository.getUserInterests(userId = userId, subjectType = subjectType))
        }.onEach {
            interests.value = it.getOrDefault(emptyList())
        }.stateInUi()

    private val hasMore =
        combine(subjectResult, interestsResult) { subjectResult, interestsResult ->
            return@combine subjectResult?.isSuccess == true && interestsResult?.isSuccess == true && subjectResult.getOrThrow().interests.sumOf { it.count } > interestsResult.getOrThrow().size
        }.stateInUi(false)

    val moreInterestsPagingData = hasMore.flatMapLatest { hasMore ->
        if (hasMore) {
            userSubjectRepository.getUserInterestsPagingData(userId, subjectType)
                .cachedIn(viewModelScope)
        } else {
            emptyFlow()
        }
    }.cachedIn(viewModelScope)

    private val isLoggedIn = authRepository.isLoggedIn()

    val interestsUiState =
        combine(
            subjectResult,
            interestsResult,
            interests,
            hasMore,
            isLoggedIn
        ) { subjectResult, interestsResult, interests, hasMore, isLoggedIn ->
            when {
                subjectResult?.isSuccess == true && interestsResult?.isSuccess == true -> {
                    val subject = subjectResult.getOrThrow()
                    val statusesWithInterests = subject.interests.map { interestStatus ->
                        Pair(
                            first = interestStatus,
                            second = interests.filter { it.interest.status == interestStatus.status })
                    }
                    InterestsUiState.Success(
                        title = subject.name,
                        statusesAndInterests = statusesWithInterests,
                        hasMore = hasMore,
                        isLoggedIn = isLoggedIn
                    )
                }

                else -> InterestsUiState.Error
            }
        }.stateInUi(InterestsUiState.Loading)

    fun onUpdateInterestStatus(
        subjectWithInterest: SubjectWithInterest<*>,
        status: SubjectInterestStatus,
    ) {
        when (status) {
            SubjectInterestStatus.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(
                        type = subjectWithInterest.type,
                        id = subjectWithInterest.id
                    )
                    if (result.isSuccess) {
                        interests.update {
                            it.toMutableList().apply {
                                remove(subjectWithInterest)
                            }
                        }
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.addSubjectToInterests(
                        subjectWithInterest.type, subjectWithInterest.id,
                        newStatus = status
                    )
                    if (result.isSuccess) {
                        interests.value = interests.value.toMutableList().apply {
                            set(indexOf(subjectWithInterest), result.getOrThrow())
                        }
                    }
                }
            }
        }
    }

}

sealed interface InterestsUiState {
    data class Success(
        val title: String,
        val statusesAndInterests: List<Pair<MySubjectStatus, List<SubjectWithInterest<*>>>>,
        val hasMore: Boolean,
        val isLoggedIn: Boolean,
    ) : InterestsUiState

    data object Error : InterestsUiState
    data object Loading : InterestsUiState
}