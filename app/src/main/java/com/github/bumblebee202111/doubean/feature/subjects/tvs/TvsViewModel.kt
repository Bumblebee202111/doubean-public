package com.github.bumblebee202111.doubean.feature.subjects.tvs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.stateInUi
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvsViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val _myTvsUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myTvsUiState = _myTvsUiState.asStateFlow()

    private val modulesResult = flow {
        emit(subjectCommonRepository.getSubjectModules(SubjectType.TV))
    }.onEach { result ->
        if (result is AppResult.Error) {
            snackbarManager.showMessage(result.error.asUiMessage())
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
            authRepository.loggedInUserId.collectLatest { userId ->
                _myTvsUiState.value = when (userId) {
                    null -> MySubjectUiState.NotLoggedIn
                    else -> {
                        val result = userSubjectRepository.getUserSubjects(
                            userId =
                                userId
                        )
                        when (result) {
                            is AppResult.Success -> {

                                MySubjectUiState.Success(
                                    userId = userId,
                                    mySubject = result.data.first {
                                        it.type == SubjectType.MOVIE
                                    })
                            }

                            is AppResult.Error -> {
                                snackbarManager.showMessage(result.error.asUiMessage())
                                MySubjectUiState.Error
                            }
                        }
                    }
                }
            }
        }
    }
}
