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
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val collectionId = savedStateHandle.toRoute<RankListRoute>().collectionId

    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val rankListResult = retryTrigger.flatMapLatest {
        flow { emit(subjectCollectionRepository.getSubjectCollection(collectionId)) }
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
        when (rankListResult) {
            is AppResult.Success -> {
                RankListUiState.Success(
                    rankList = rankListResult.data,
                    isLoggedIn = isLoggedIn
                )
            }

            is AppResult.Error -> {
                RankListUiState.Error(rankListResult.error.asUiMessage())
            }
        }
    }.stateInUi(RankListUiState.Loading)

    fun onMarkSubject(subject: SubjectWithRankAndInterest<*>) {
        viewModelScope.launch {
            val result = userSubjectRepository.addSubjectToInterests(
                type = subject.type,
                id = subject.id,
                newStatus = SubjectInterestStatus.MARK_STATUS_MARK
            )
            when (result) {
                is AppResult.Success -> {
                    val updatedItem = result.data
                    updatedItems.update { oldUpdatedItems ->
                        val existingIndex = oldUpdatedItems.indexOfFirst { it.id == updatedItem.id }
                        if (existingIndex != -1) {
                            oldUpdatedItems.toMutableList()
                                .apply { this[existingIndex] = updatedItem }
                        } else {
                            oldUpdatedItems + updatedItem
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
}