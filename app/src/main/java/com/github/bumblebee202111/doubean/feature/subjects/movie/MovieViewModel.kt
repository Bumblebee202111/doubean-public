package com.github.bumblebee202111.doubean.feature.subjects.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.MovieRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.MovieRoute
import com.github.bumblebee202111.doubean.model.Movie
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userSubjectRepository: UserSubjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val movieId = savedStateHandle.toRoute<MovieRoute>().movieId
    private val movie = MutableStateFlow<SubjectWithInterest<Movie>?>(null)
    private val movieResult = flow {
        emit(movieRepository.getMovie(movieId))
    }.onEach {
        movie.value = it.getOrNull()
    }
    val movieUiState = combine(movie, movieResult) { movie, movieResult ->
        if (movieResult.isSuccess) {
            MovieUiState.Success(movie!!)
        } else {
            MovieUiState.Error
        }
    }.stateInUi(MovieUiState.Loading)

    fun onUpdateStatus(
        subjectWithInterest: SubjectWithInterest<Movie>,
        status: SubjectInterest.Status,
    ) {
        when (status) {
            SubjectInterest.Status.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(subjectWithInterest.subject)
                    if (result.isSuccess) {
                        movie.value = movie.value?.let {
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
                        movie.value = result.getOrThrow()
                    }
                }
            }
        }
    }

}