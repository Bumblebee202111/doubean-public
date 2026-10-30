package com.github.bumblebee202111.doubean.feature.subjects.elessar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.subject.SubjectCommonRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel(assistedFactory = ElessarSubjectViewModel.Factory::class)
class ElessarSubjectViewModel @AssistedInject constructor(
    private val subjectCommonRepository: SubjectCommonRepository,
    private val snackbarManager: SnackbarManager,
    @Assisted val subjectId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ElessarSubjectUiState>(ElessarSubjectUiState.Loading)
    val uiState: StateFlow<ElessarSubjectUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        fetchData()
    }

    
    fun fetchData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = ElessarSubjectUiState.Loading
            when (val result = subjectCommonRepository.getElessarSubject(subjectId)) {
                is AppResult.Success -> {
                    _uiState.value = ElessarSubjectUiState.Success(result.data)
                }

                is AppResult.Error -> {
                    val uiMessage = result.error.asUiMessage()
                    snackbarManager.showMessage(uiMessage)
                    _uiState.value = ElessarSubjectUiState.Error(uiMessage)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(subjectId: String): ElessarSubjectViewModel
    }
}