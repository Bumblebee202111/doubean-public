package com.github.bumblebee202111.doubean.feature.subjects.tv

import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subjects.TvDetail

sealed interface TvUiState {
    data class Success(
        val tv: TvDetail,
        val interests: SubjectInterestWithUserList,
        val photos: PhotoList,
        val reviews: SubjectReviewList,
        val isLoggedIn: Boolean,
    ) : TvUiState

    data object Loading : TvUiState
    data object Error : TvUiState
}

