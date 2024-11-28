package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.InterestsRoute
import com.github.bumblebee202111.doubean.model.MySubjectStatus
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
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
    }

    private val interests = MutableStateFlow<List<SubjectWithInterest<*>>>(emptyList())

    private val interestsResult =
        flow {
            emit(userSubjectRepository.getUserInterests(userId = userId, subjectType = subjectType))
        }.onEach {
            interests.value = it.getOrDefault(emptyList())
        }

    private val isLoggedIn = authRepository.isLoggedIn()

    val interestsUiState =
        combine(
            subjectResult,
            interestsResult,
            interests,
            isLoggedIn
        ) { subjectResult, interestsResult, interests, isLoggedIn ->
            when {
                subjectResult.isSuccess && interestsResult.isSuccess -> {
                    val subject = subjectResult.getOrThrow()
                    val statusesWithInterests = subject.interests.map { interestStatus ->
                        Pair(
                            first = interestStatus,
                            second = interests.filter { it.interest.status == interestStatus.status })
                    }
                    InterestsUiState.Success(
                        title = subject.name,
                        interests = statusesWithInterests,
                        isLoggedIn = isLoggedIn
                    )
                }

                else -> InterestsUiState.Error
            }
        }.stateInUi(InterestsUiState.Loading)

    fun onUpdateInterestStatus(
        subjectWithInterest: SubjectWithInterest<*>,
        status: SubjectInterest.Status,
    ) {
        when (status) {
            SubjectInterest.Status.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(subjectWithInterest.subject)
                    if (result.isSuccess) {
                        interests.value = interests.value.toMutableList().apply {
                            remove(subjectWithInterest)
                        }
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.addSubjectToInterests(
                        subject = subjectWithInterest.subject,
                        newStatus = status
                    )
                    if (result.isSuccess) {
                        interests.value = interests.value.toMutableList().apply {
                            set(indexOf(subjectWithInterest), result.getOrThrow())
                        }
                    }
                    interests
                }
            }
        }
    }

}

sealed interface InterestsUiState {
    data class Success(
        val title: String,
        val interests: List<Pair<MySubjectStatus, List<SubjectWithInterest<*>>>>,
        val isLoggedIn: Boolean,
    ) : InterestsUiState

    data object Error : InterestsUiState
    data object Loading : InterestsUiState
}