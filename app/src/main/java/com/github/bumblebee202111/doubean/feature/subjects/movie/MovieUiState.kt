package com.github.bumblebee202111.doubean.feature.subjects.movie

import com.github.bumblebee202111.doubean.model.MovieDetail
import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.SubjectReviewList

sealed interface MovieUiState {
    data class Success(
        val movie: MovieDetail,
        val interests: SubjectInterestWithUserList,
        val photos: PhotoList,
        val reviews: SubjectReviewList,
        val isLoggedIn: Boolean,
    ) : MovieUiState
    data object Loading : MovieUiState
    data object Error : MovieUiState
}

