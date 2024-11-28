package com.github.bumblebee202111.doubean.feature.subjects.movie

import com.github.bumblebee202111.doubean.model.Movie
import com.github.bumblebee202111.doubean.model.SubjectWithInterest

sealed interface MovieUiState {
    data class Success(val movie: SubjectWithInterest<Movie>) : MovieUiState
    data object Loading : MovieUiState
    data object Error : MovieUiState
}

