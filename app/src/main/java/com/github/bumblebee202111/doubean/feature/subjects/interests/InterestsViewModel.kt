package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.InterestsRoute
import com.github.bumblebee202111.doubean.model.MySubjectStatus
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
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

    private val interestsResult =
        flow {
            emit(userSubjectRepository.getUserInterests(userId, subjectType))
        }

    val interestsUiState =
        subjectResult.combine(interestsResult) { subjectResult, interestsResult ->
            when {
                subjectResult.isSuccess && interestsResult.isSuccess -> {
                    val subject = subjectResult.getOrThrow()
                    val interests = interestsResult.getOrThrow()
                    val statusesWithInterests = subject.interests.map { interestStatus ->
                        Pair(
                            interestStatus,
                            interests.filter { it.status == interestStatus.titleEng })
                    }
                    InterestsUiState.Success(
                        title = subject.name,
                        interests = statusesWithInterests
                    )
                }

                else -> InterestsUiState.Error
            }
        }.stateInUi(InterestsUiState.Loading)

}

sealed interface InterestsUiState {
    data class Success(
        val title: String,
        val interests: List<Pair<MySubjectStatus, List<SubjectInterest>>>,
    ) : InterestsUiState

    data object Error : InterestsUiState
    data object Loading : InterestsUiState
}