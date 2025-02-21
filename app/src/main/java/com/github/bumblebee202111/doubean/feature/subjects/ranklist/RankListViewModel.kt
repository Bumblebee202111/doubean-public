package com.github.bumblebee202111.doubean.feature.subjects.ranklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCollectionRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.RankListRoute
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankListViewModel @Inject constructor(
    private val subjectCollectionRepository: SubjectCollectionRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val collectionId = savedStateHandle.toRoute<RankListRoute>().collectionId

    private val rankListResult = flow {
        emit(subjectCollectionRepository.getSubjectCollection(collectionId))
    }

    private val updatedItems = MutableStateFlow(listOf<SubjectWithInterest<*>>())
    val itemsPagingData =
        subjectCollectionRepository.getSubjectCollectionItemsPagingData(collectionId)
            .cachedIn(viewModelScope).combine(updatedItems) { pagingData, updatedItem ->

                pagingData.map { pagingDataItem ->
                    updatedItem.find { it.id == pagingDataItem.id }?.let {
                        pagingDataItem.copy(interest = it.interest)
                    } ?: pagingDataItem
                }

            }.cachedIn(viewModelScope)

    private val isLoggedIn = authRepository.isLoggedIn()

    val rankListUiState = combine(
        rankListResult,
        isLoggedIn
    ) { rankListResult, isLoggedIn ->
        if (rankListResult.isSuccess) {
            RankListUiState.Success(
                rankList = rankListResult.getOrThrow(),
                isLoggedIn
            )
        } else {
            RankListUiState.Error
        }
    }.stateInUi(RankListUiState.Loading)

    fun onMarkSubject(subject: SubjectWithRankAndInterest<*>) {
        viewModelScope.launch {
            val result = userSubjectRepository.addSubjectToInterests(
                type = subject.type, id = subject.id,
                newStatus = SubjectInterestStatus.MARK_STATUS_MARK
            )
            if (result.isSuccess) {
                updatedItems.update { oldUpdatedItems ->
                    oldUpdatedItems.filterNot { it.id == subject.id } + result.getOrThrow()
                }
            }
        }
    }
}