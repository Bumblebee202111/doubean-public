package com.github.bumblebee202111.doubean.feature.subjects.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.subject.SubjectCommonRepository
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subject.ReviewDetail
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.component.RexxarWebViewPool
import com.github.bumblebee202111.doubean.ui.model.UiMessage
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal fun reviewWebViewPoolKey(reviewId: String) = "review_$reviewId"

sealed interface ReviewUiState {
    data object Loading : ReviewUiState
    data class Success(val review: ReviewDetail) : ReviewUiState
    data class Error(val message: UiMessage) : ReviewUiState
}


@HiltViewModel(assistedFactory = ReviewViewModel.Factory::class)
class ReviewViewModel @AssistedInject constructor(
    private val subjectCommonRepository: SubjectCommonRepository,
    private val snackbarManager: SnackbarManager,
    @Assisted val reviewId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        fetchReview()
    }

    
    fun fetchReview() {
        viewModelScope.launch {
            _uiState.value = ReviewUiState.Loading
            when (val result = subjectCommonRepository.getReview(reviewId)) {
                is AppResult.Success -> {
                    _uiState.value = ReviewUiState.Success(result.data)
                }

                is AppResult.Error -> {
                    val uiMessage = result.error.asUiMessage()
                    snackbarManager.showMessage(uiMessage)
                    _uiState.value = ReviewUiState.Error(uiMessage)
                }
            }
        }
    }

    fun displayInvalidImageUrl() {
        snackbarManager.showMessage("Invalid image Url".toUiMessage())
    }

    override fun onCleared() {
        super.onCleared()
        RexxarWebViewPool.release(reviewWebViewPoolKey(reviewId))
    }

    @AssistedFactory
    interface Factory {
        fun create(reviewId: String): ReviewViewModel
    }
}