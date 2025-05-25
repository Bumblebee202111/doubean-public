@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.InterestsRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.MySubjectStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) :
    ViewModel() {

    private val route = savedStateHandle.toRoute<InterestsRoute>()
    private val userId = route.userId
    private val subjectType = route.subjectType
    private val subjectResult = flow {
        emit(userSubjectRepository.getUserSubjects(userId))
    }.mapLatest { result ->
        when (result) {
            is AppResult.Success -> {
                AppResult.Success(result.data.first { it.type == subjectType })
            }

            is AppResult.Error -> {
                snackbarManager.showMessage(result.error.asUiMessage())
                result
            }
        }

    }.stateInUi()

    private val interests = MutableStateFlow<List<SubjectWithInterest<*>>>(emptyList())

    private val interestsResult =
        flow {
            emit(userSubjectRepository.getUserInterests(userId = userId, subjectType = subjectType))
        }.onEach { result ->
            when (result) {
                is AppResult.Success -> interests.value = result.data
                is AppResult.Error -> {
                    interests.value = emptyList()
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }
        }.stateInUi()

    private val hasMore =
        combine(subjectResult, interestsResult) { subjectResult, interestsResult ->
            if (subjectResult is AppResult.Success && interestsResult is AppResult.Success) {
                subjectResult.data.interests.sumOf { it.count } > interestsResult.data.size
            } else {
                false
            }
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
                subjectResult is AppResult.Success && interestsResult is AppResult.Success -> {
                    val subject = subjectResult.data
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

                subjectResult == null || interestsResult == null -> {
                    InterestsUiState.Loading
                }

                else -> {
                    InterestsUiState.Error
                }
            }
        }.stateInUi(InterestsUiState.Loading)

    fun onUpdateInterestStatus(
        subjectWithInterest: SubjectWithInterest<*>,
        status: SubjectInterestStatus,
    ) {
        viewModelScope.launch {
            val result: AppResult<Any> = when (status) {
                SubjectInterestStatus.MARK_STATUS_UNMARK -> {
                    userSubjectRepository.unmarkSubject(
                        type = subjectWithInterest.type,
                        id = subjectWithInterest.id
                    )
                }

                else -> {
                    userSubjectRepository.addSubjectToInterests(
                        subjectWithInterest.type, subjectWithInterest.id,
                        newStatus = status
                    )
                }
            }

            when (result) {
                is AppResult.Success -> {
                    if (status == SubjectInterestStatus.MARK_STATUS_UNMARK) {
                        interests.update { list ->
                            list.filterNot { it.id == subjectWithInterest.id && it.type == subjectWithInterest.type }
                        }
                    } else if (result.data is SubjectWithInterest<*>) {
                        interests.update { list ->
                            list.map { item ->
                                if (item.id == subjectWithInterest.id && item.type == subjectWithInterest.type) {
                                    result.data
                                } else {
                                    item
                                }
                            }
                        }
                    }
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
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