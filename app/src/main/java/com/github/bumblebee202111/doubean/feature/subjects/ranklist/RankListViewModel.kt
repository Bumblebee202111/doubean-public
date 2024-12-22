package com.github.bumblebee202111.doubean.feature.subjects.ranklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCollectionRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.RankListRoute
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
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

    private val items: MutableStateFlow<List<SubjectWithRankAndInterest<*>>> =
        MutableStateFlow(
            emptyList()
        )

    private val rankListResult = flow {
        emit(subjectCollectionRepository.getSubjectCollection(collectionId))
    }

    private val itemsResult =
        flow { emit(subjectCollectionRepository.getSubjectCollectionItems(collectionId)) }.onEach {
            if (it.isSuccess) {
                items.value = it.getOrDefault(emptyList())
            }
        }

    private val isLoggedIn = authRepository.isLoggedIn()

    val rankListUiState = combine(
        rankListResult,
        itemsResult,
        items,
        isLoggedIn
    ) { rankListResult, itemsResult, items, isLoggedIn ->
        if (rankListResult.isSuccess && itemsResult.isSuccess) {
            RankListUiState.Success(
                rankList = rankListResult.getOrThrow(),
                items = items,
                isLoggedIn
            )
        } else {
            RankListUiState.Error
        }
    }.stateInUi(RankListUiState.Loading)

    fun onMarkSubject(subject: SubjectWithRankAndInterest<*>) {
        viewModelScope.launch {
            val result = userSubjectRepository.addSubjectToInterests(
                subject.type, subject.id,
                newStatus = SubjectInterestStatus.MARK_STATUS_MARK
            )
            if (result.isSuccess) {
                items.value = items.value.toMutableList().apply {
                    set(indexOf(subject), subject.copy(interest = result.getOrThrow().interest))
                }
            }
        }
    }
}