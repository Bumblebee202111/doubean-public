package com.github.bumblebee202111.doubean.feature.subjects.movie

import com.github.bumblebee202111.doubean.feature.subjects.common.InterestSortType
import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.MovieDetail
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface MovieUiState {
    data class Success(
        val movie: MovieDetail,
        val interests: SubjectInterestWithUserList,
        val interestSortType: InterestSortType = InterestSortType.DEFAULT,
        val photos: PhotoList,
        val recommendations: List<RecommendSubject>,
        val reviews: SubjectReviewList,
        val isLoggedIn: Boolean,

        ) : MovieUiState
    data object Loading : MovieUiState
    data class Error(val message: UiMessage) : MovieUiState
}