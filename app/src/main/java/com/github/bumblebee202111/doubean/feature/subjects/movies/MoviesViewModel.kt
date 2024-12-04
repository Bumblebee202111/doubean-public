package com.github.bumblebee202111.doubean.feature.subjects.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.MovieRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.Subject
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectCollection
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _myMoviesUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myMoviesUiState = _myMoviesUiState.asStateFlow()

    private val collectionItems: MutableStateFlow<List<SubjectWithInterest<*>>> =
        MutableStateFlow(
            emptyList()
        )

    private val collectionResult = flow {
        emit(movieRepository.getTop250MoviesCollection().map(NetworkSubjectCollection::title))
    }

    private val collectionItemsResult =
        flow { emit(movieRepository.getTop250MoviesCollectionItems()) }.onEach {
            if (it.isSuccess) {
                collectionItems.value = it.getOrDefault(emptyList())
            }
        }

    val isLoggedIn = authRepository.isLoggedIn()

    val moviesUiState = combine(
        collectionResult,
        collectionItemsResult,
        isLoggedIn,
        collectionItems
    ) { collectionResult, collectionItemsResult, isLoggedIn, collectionItems ->
        when {
            collectionResult.isSuccess && collectionItemsResult.isSuccess -> {
                MoviesUiState.Success(
                    title = collectionResult.getOrThrow(),
                    items = collectionItems,
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
            }.collect()
        }
    }

    fun onMarkMovie(movie: SubjectWithInterest<*>) {
        viewModelScope.launch {
            val result = userSubjectRepository.addSubjectToInterests<Subject>(
                movie.type, movie.id,
                newStatus = SubjectInterestStatus.MARK_STATUS_MARK
            )
            if (result.isSuccess) {
                collectionItems.value = collectionItems.value.toMutableList().apply {
                    set(indexOf(movie), result.getOrThrow())
                }
            }
        }
    }

}

sealed interface MoviesUiState {
    data class Success(
        val title: String,
        val items: List<SubjectWithInterest<*>>,
        val isLoggedIn: Boolean,
    ) : MoviesUiState

    data object Error : MoviesUiState
    data object Loading : MoviesUiState
}

