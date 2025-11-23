package com.github.bumblebee202111.doubean.feature.subjects.book

import com.github.bumblebee202111.doubean.model.subjects.BookDetail
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface BookUiState {
    data class Success(
        val book: BookDetail,
        val interests: SubjectInterestWithUserList,
        val recommendations: List<RecommendSubject>,
        val reviews: SubjectReviewList,
        val isLoggedIn: Boolean,
    ) : BookUiState

    data object Loading : BookUiState
    data class Error(val message: UiMessage) : BookUiState
}

