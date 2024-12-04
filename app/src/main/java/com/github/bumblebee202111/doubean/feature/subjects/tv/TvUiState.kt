package com.github.bumblebee202111.doubean.feature.subjects.tv

import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.TvDetail

sealed interface TvUiState {
    data class Success(
        val tv: TvDetail,
        val interests: SubjectInterestWithUserList,
        val photos: PhotoList,
        val isLoggedIn: Boolean,
    ) : TvUiState

    data object Loading : TvUiState
    data object Error : TvUiState
}

