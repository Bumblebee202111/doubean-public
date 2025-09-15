package com.github.bumblebee202111.doubean.feature.subjects.movies

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
class MoviesViewModel @Inject constructor(
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val _myMoviesUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myMoviesUiState = _myMoviesUiState.asStateFlow()

    private val modulesResult = flow {
        emit(subjectCommonRepository.getSubjectModules(SubjectType.MOVIE))
    }.onEach { result ->
        if (result is AppResult.Error) {
            snackbarManager.showMessage(result.error.asUiMessage())
        }
    }

    val isLoggedIn = authRepository.isLoggedIn()

    val modulesUiState = combine(
        isLoggedIn,
        modulesResult
    ) { isLoggedIn, modulesResult ->
        when (modulesResult) {
            is AppResult.Success -> {
                SubjectModulesUiState.Success(
                    modules = modulesResult.data,
                    isLoggedIn = isLoggedIn
                )
            }

            is AppResult.Error -> {
                SubjectModulesUiState.Error(modulesResult.error)
            }
        }
    }.stateInUi(SubjectModulesUiState.Loading)

    init {
        getMyMovies()
    }

    private fun getMyMovies() {
        viewModelScope.launch {
            authRepository.observeLoggedInUserId().collectLatest { userId ->
                _myMoviesUiState.value = when (userId) {
                    null -> MySubjectUiState.NotLoggedIn
                    else -> {
                        when (val result = userSubjectRepository.getUserSubjects(userId = userId)) {
                            is AppResult.Success ->
                                MySubjectUiState.Success(
                                    userId = userId,
                                    mySubject = result.data.first {
                                        it.type == SubjectType.MOVIE
                                    }
                                )

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

