package com.github.bumblebee202111.doubean.feature.subjects.detail

import com.github.bumblebee202111.doubean.feature.subjects.common.InterestSortType
import com.github.bumblebee202111.doubean.model.PhotoList
import com.github.bumblebee202111.doubean.model.subjects.CreditList
import com.github.bumblebee202111.doubean.model.subjects.RecommendSubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestWithUserList
import com.github.bumblebee202111.doubean.model.subjects.SubjectReviewList
import com.github.bumblebee202111.doubean.ui.model.UiMessage

sealed interface SubjectUiState {
    data class Success(
        val subject: SubjectDetail,
        val creditList: CreditList?,
        val photos: PhotoList?,
        val interests: SubjectInterestWithUserList,
        val interestSortType: InterestSortType = InterestSortType.DEFAULT,
        val recommendations: List<RecommendSubject>,
        val reviews: SubjectReviewList,
        val isLoggedIn: Boolean,
    ) : SubjectUiState

    data object Loading : SubjectUiState
    data class Error(val message: UiMessage) : SubjectUiState
}