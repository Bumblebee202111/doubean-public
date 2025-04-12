package com.github.bumblebee202111.doubean.feature.subjects.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.coroutines.combine
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.MovieRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.MovieRoute
import com.github.bumblebee202111.doubean.model.subjects.MovieDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val movieId = savedStateHandle.toRoute<MovieRoute>().movieId
    private val movie = MutableStateFlow<MovieDetail?>(null)
    private val movieResult = flow {
        emit(movieRepository.getMovie(movieId))
    }.onEach {
        movie.value = it.getOrNull()
    }
    private val interestList = flow {
        emit(
            userSubjectRepository.getSubjectDoneFollowingHotInterests(SubjectType.MOVIE, movieId)
        )
    }
    private val photos = flow {
        emit(
            movieRepository.getPhotos(movieId)
        )
    }
    private val reviews = flow {
        emit(
            subjectCommonRepository.getSubjectReviews(
                subjectType = SubjectType.MOVIE,
                subjectId = movieId
            )
        )
    }
    private val isLoggedIn = authRepository.isLoggedIn()
    val movieUiState = combine(
        movie,
        movieResult,
        interestList,
        photos,
        reviews,
        isLoggedIn
    ) { movie, movieResult, interestList, photos, reviews, isLoggedIn ->
        if (movieResult.isSuccess && interestList.isSuccess && photos.isSuccess && reviews.isSuccess) {
            MovieUiState.Success(
                movie = movie!!,
                interests = interestList.getOrThrow(),
                photos = photos.getOrThrow(),
                reviews = reviews.getOrThrow(),
                isLoggedIn = isLoggedIn
            )
        } else {
            MovieUiState.Error
        }
    }.stateInUi(MovieUiState.Loading)

    fun onUpdateStatus(
        status: SubjectInterestStatus,
    ) {
        val oldMovie = movie.value ?: return
        when (status) {
            SubjectInterestStatus.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(oldMovie.type, oldMovie.id)
                    if (result.isSuccess) {
                        movie.value = movie.value?.copy(
                            interest = SubjectInterest(
                                null,
                                SubjectInterestStatus.MARK_STATUS_UNMARK
                            )
                        )
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.addSubjectToInterests(
                        type = oldMovie.type, id = oldMovie.id,
                        newStatus = status
                    )
                    if (result.isSuccess) {
                        movie.update {
                            it?.copy(interest = result.getOrThrow().interest)
                        }
                    }
                }
            }
        }
    }

}