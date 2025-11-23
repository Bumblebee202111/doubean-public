package com.github.bumblebee202111.doubean.feature.subjects.tv

import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.model.subjects.TvDetail
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface TvUiState {
    data class Success(
        val tv: TvDetail,
        val interests: SubjectInterestWithUserList,
        val photos: PhotoList,
        val recommendations: List<RecommendSubject>,
        val reviews: SubjectReviewList,
        val isLoggedIn: Boolean,
    ) : TvUiState

    data object Loading : TvUiState
    data class Error(val message: UiMessage) : TvUiState
}

