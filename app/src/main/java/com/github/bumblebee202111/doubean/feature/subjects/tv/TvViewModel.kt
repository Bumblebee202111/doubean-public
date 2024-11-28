package com.github.bumblebee202111.doubean.feature.subjects.tv

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.TvRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.TvRoute
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.Tv
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvViewModel @Inject constructor(
    private val tvRepository: TvRepository,
    private val userSubjectRepository: UserSubjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val tvId = savedStateHandle.toRoute<TvRoute>().tvId
    private val tv = MutableStateFlow<SubjectWithInterest<Tv>?>(null)
    private val tvResult = flow {
        emit(tvRepository.getTv(tvId))
    }.onEach {
        tv.value = it.getOrNull()
    }
    val tvUiState = combine(tv, tvResult) { tv, tvResult ->
        if (tvResult.isSuccess) {
            TvUiState.Success(tv!!)
        } else {
            TvUiState.Error
        }
    }.stateInUi(TvUiState.Loading)

    fun onUpdateStatus(
        subjectWithInterest: SubjectWithInterest<Tv>,
        status: SubjectInterest.Status,
    ) {
        when (status) {
            SubjectInterest.Status.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(subjectWithInterest.subject)
                    if (result.isSuccess) {
                        tv.value = tv.value?.let {
                            SubjectWithInterest(subject = it.subject)
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
                        tv.value = result.getOrThrow()
                    }
                }
            }
        }
    }

}