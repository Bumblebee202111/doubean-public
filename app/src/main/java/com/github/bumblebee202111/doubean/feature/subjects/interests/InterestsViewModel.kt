@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.GenericError
import com.github.bumblebee202111.doubean.model.subjects.MySubjectStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.UiMessage
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

@HiltViewModel(assistedFactory = InterestsViewModel.Factory::class)
class InterestsViewModel @AssistedInject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
    @Assisted userId: String,
    @Assisted subjectType: SubjectType,
) :
    ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    private val subjectResult = retryTrigger.flatMapLatest {
        flow {
            emit(userSubjectRepository.getUserSubjects(userId))
        }
    }.mapLatest { result ->
        when (result) {
            is AppResult.Success -> {
                AppResult.Success(result.data.first { it.type == subjectType })
            }

            is AppResult.Error -> {
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
                    val error = (subjectResult as? AppResult.Error)?.error
                        ?: (interestsResult as? AppResult.Error)?.error
                        ?: GenericError(null)

                    InterestsUiState.Error(error.asUiMessage())
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

    fun retry() {
        retryTrigger.value++
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String, subjectType: SubjectType): InterestsViewModel
    }
}

sealed interface InterestsUiState {
    data class Success(
        val title: String,
        val statusesAndInterests: List<Pair<MySubjectStatus, List<SubjectWithInterest<*>>>>,
        val hasMore: Boolean,
        val isLoggedIn: Boolean,
    ) : InterestsUiState

    data class Error(val message: UiMessage) : InterestsUiState
    data object Loading : InterestsUiState
}