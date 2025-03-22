package com.github.bumblebee202111.doubean.feature.subjects.tvs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvsViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiErrors = MutableStateFlow(emptyList<AppError>())
    val uiErrors = _uiErrors.asStateFlow()

    private val _myTvsUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myMoviesUiState = _myTvsUiState.asStateFlow()

    private val modulesResult = flow {
        emit(subjectCommonRepository.getSubjectModules(SubjectType.TV))
    }.onEach { result ->
        if (result is AppResult.Error) {
            _uiErrors.update {
                it + result.error
            }
        }
    }

    val isLoggedIn = authRepository.isLoggedIn()

    val modulesUiState = combine(
        isLoggedIn,
        modulesResult,
    ) { isLoggedIn, modules ->
        when (modules) {
            is AppResult.Success -> {
                SubjectModulesUiState.Success(modules = modules.data, isLoggedIn = isLoggedIn)
            }

            is AppResult.Error ->
                SubjectModulesUiState.Error(modules.error)
        }
    }.stateInUi(SubjectModulesUiState.Loading)

    init {
        getMyTvs()
    }

    private fun getMyTvs() {
        viewModelScope.launch {
            authRepository.observeLoggedInUserId().collectLatest { userId ->
                _myTvsUiState.value = when (userId) {
                    null -> MySubjectUiState.NotLoggedIn
                    else -> {
                        val result = userSubjectRepository.getUserSubjects(
                            userId =
                            userId
                        )
                        when (result.isSuccess) {
                            true ->
                                MySubjectUiState.Success(
                                    userId = userId,
                                    mySubject = result.getOrThrow().first {
                                        it.type == SubjectType.MOVIE
                                    }
                                )

                            false -> MySubjectUiState.Error
                        }
                    }
                }
            }
        }
    }

    fun onErrorShown(error: AppError) {
        _uiErrors.update { oldUiErrors ->
            oldUiErrors - error
        }
    }
}
