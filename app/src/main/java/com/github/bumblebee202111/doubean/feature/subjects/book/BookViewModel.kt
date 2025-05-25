package com.github.bumblebee202111.doubean.feature.subjects.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.BookRepository
import com.github.bumblebee202111.doubean.data.repository.SubjectCommonRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.book.navigation.BookRoute
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterest
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    private val bookId = savedStateHandle.toRoute<BookRoute>().bookId
    private val _uiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private var loadDataJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn()
                .distinctUntilChanged()
                .collect { isLoggedIn ->
                    triggerDataLoad(isLoggedIn)
                }
        }
    }


    private fun triggerDataLoad(isLoggedIn: Boolean) {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            _uiState.value = BookUiState.Loading

            val bookResultDeferred = async { bookRepository.getBook(bookId) }
            val interestsResultDeferred = async {
                userSubjectRepository.getSubjectDoneFollowingHotInterests(
                    type = SubjectType.BOOK,
                    id = bookId
                )
            }
            val reviewsResultDeferred = async {
                subjectCommonRepository.getSubjectReviews(
                    subjectType = SubjectType.BOOK,
                    subjectId = bookId
                )
            }

            val bookResult = bookResultDeferred.await()
            val interestResult = interestsResultDeferred.await()
            val reviewsResult = reviewsResultDeferred.await()

            listOf(bookResult, interestResult, reviewsResult).forEach { result ->
                if (result is AppResult.Error) {
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }

            if (bookResult is AppResult.Success &&
                interestResult is AppResult.Success &&
                reviewsResult is AppResult.Success
            ) {
                _uiState.value = BookUiState.Success(
                    book = bookResult.data,
                    interests = interestResult.data,
                    reviews = reviewsResult.data,
                    isLoggedIn = isLoggedIn
                )
            } else {
                _uiState.value = BookUiState.Error
            }
        }
    }

    suspend fun refreshData() {
        val currentLoginStatus = authRepository.isLoggedIn().first()
        triggerDataLoad(currentLoginStatus)
    }

    fun onUpdateStatus(newStatus: SubjectInterestStatus) {
        val currentSuccessState = _uiState.value as? BookUiState.Success ?: return

        if (!currentSuccessState.isLoggedIn) {
            return
        }

        val originalBook = currentSuccessState.book

        viewModelScope.launch {
            val result: AppResult<Any> = when (newStatus) {
                SubjectInterestStatus.MARK_STATUS_UNMARK ->
                    userSubjectRepository.unmarkSubject(originalBook.type, originalBook.id)

                else ->
                    userSubjectRepository.addSubjectToInterests(
                        type = originalBook.type,
                        id = originalBook.id,
                        newStatus = newStatus
                    )
            }

            when (result) {
                is AppResult.Success -> {
                    val confirmedInterest: SubjectInterest? = when (val data = result.data) {
                        is SubjectInterest -> data
                        is SubjectWithInterest<*> -> data.interest
                        else -> {
                            // Unreachable
                            originalBook.interest // Keep the old interest
                        }
                    }

                    val confirmedBook = originalBook.copy(interest = confirmedInterest)

                    (_uiState.value as? BookUiState.Success)?.takeIf { it.book.id == originalBook.id }
                        ?.let { latestSuccessState ->
                            _uiState.value = latestSuccessState.copy(book = confirmedBook)
                        }
                }

                is AppResult.Error -> {
                    snackbarManager.showMessage(result.error.asUiMessage())
                }
            }
        }
    }

}