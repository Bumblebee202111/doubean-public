package com.github.bumblebee202111.doubean.feature.subjects.movie

import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.MovieDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList

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

