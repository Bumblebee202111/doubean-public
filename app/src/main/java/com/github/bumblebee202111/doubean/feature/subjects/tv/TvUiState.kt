package com.github.bumblebee202111.doubean.feature.subjects.tv

import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.Tv

sealed interface TvUiState {
    data class Success(val tv: SubjectWithInterest<Tv>, val isLoggedIn: Boolean) : TvUiState
    data object Loading : TvUiState
    data object Error : TvUiState
}

