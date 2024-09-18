package com.github.bumblebee202111.doubean.feature.subjects.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.MovieRepository
import com.github.bumblebee202111.doubean.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
) : ViewModel() {
    init {
        getTop250MoviesCollectionWithItems()
    }

    private val _moviesUiState: MutableStateFlow<MoviesUiState> =
        MutableStateFlow(MoviesUiState.Loading)
    val moviesUiState = _moviesUiState.asStateFlow()
    private fun getTop250MoviesCollectionWithItems() {
        viewModelScope.launch {
            val collectionResult =
                async {
                    movieRepository.getTop250MoviesCollection().map { it.title }
                }.await()
            val collectionItemsResult =
                async {
                    movieRepository.getTop250MoviesCollectionItems()
                }.await()

            _moviesUiState.value = when {
                collectionResult.isSuccess && collectionItemsResult.isSuccess -> {
                    MoviesUiState.Success(
                        collectionResult.getOrThrow(),
                        collectionItemsResult.getOrThrow()
                    )
                }

                else -> MoviesUiState.Error
            }
        }
    }
}

sealed interface MoviesUiState {
    data class Success(
        val title: String,
        val items: List<Movie>,
    ) : MoviesUiState

    data object Error : MoviesUiState
    data object Loading : MoviesUiState
}