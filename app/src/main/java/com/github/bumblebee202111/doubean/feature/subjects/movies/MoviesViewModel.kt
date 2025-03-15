package com.github.bumblebee202111.doubean.feature.subjects.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.SubjectModule
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.stateInUi
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
) : ViewModel() {

    private val _myMoviesUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myMoviesUiState = _myMoviesUiState.asStateFlow()


    private val modulesResult = flow {
        emit(subjectCommonRepository.getSubjectModules(SubjectType.MOVIE))
    }

    val isLoggedIn = authRepository.isLoggedIn()

    val moviesUiState = combine(
        isLoggedIn,
        modulesResult,
    ) { isLoggedIn, modules ->
        when {
            modules.isSuccess -> {
                MoviesUiState.Success(
                    modules = modules.getOrThrow(),
                    isLoggedIn = isLoggedIn
                )
            }

            else ->
                MoviesUiState.Error
        }
    }.stateInUi(MoviesUiState.Loading)

    init {
        getMyMovies()
    }

    private fun getMyMovies() {
        viewModelScope.launch {
            authRepository.observeLoggedInUserId().onEach { userId ->
                _myMoviesUiState.value = when (userId) {
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
            }.collectLatest { }
        }
    }



}

sealed interface MoviesUiState {
    data class Success(
        val modules: List<SubjectModule>,
        val isLoggedIn: Boolean,
    ) : MoviesUiState

    data object Error : MoviesUiState
    data object Loading : MoviesUiState
}

