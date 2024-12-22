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
import com.github.bumblebee202111.doubean.model.BookDetail
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val subjectCommonRepository: SubjectCommonRepository,
    authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val bookId = savedStateHandle.toRoute<BookRoute>().bookId
    private val book = MutableStateFlow<BookDetail?>(null)
    private val bookResult = flow {
        emit(bookRepository.getBook(bookId))
    }.onEach {
        book.value = it.getOrNull()
    }
    private val interestList = flow {
        emit(
            userSubjectRepository.getSubjectDoneFollowingHotInterests(SubjectType.BOOK, bookId)
        )
    }
    private val reviews = flow {
        emit(
            subjectCommonRepository.getSubjectReviews(
                subjectType = SubjectType.BOOK,
                subjectId = bookId
            )
        )
    }
    private val isLoggedIn = authRepository.isLoggedIn()
    val bookUiState = combine(
        book,
        bookResult,
        interestList,
        reviews,
        isLoggedIn
    ) { book, bookResult, interestList, reviews, isLoggedIn ->
        if (bookResult.isSuccess) {
            BookUiState.Success(
                book = book!!,
                interests = interestList.getOrThrow(),
                reviews = reviews.getOrThrow(),
                isLoggedIn = isLoggedIn
            )
        } else {
            BookUiState.Error
        }
    }.stateInUi(BookUiState.Loading)

    fun onUpdateStatus(
        newStatus: SubjectInterestStatus,
    ) {
        val oldBook = book.value ?: return
        when (newStatus) {
            SubjectInterestStatus.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(oldBook.type, oldBook.id)
                    if (result.isSuccess) {
                        book.value =
                            book.value?.copy(
                                interest = SubjectInterest(
                                    null,
                                    SubjectInterestStatus.MARK_STATUS_UNMARK
                                )
                            )
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.addSubjectToInterests(
                        type = oldBook.type,
                        id = oldBook.id,
                        newStatus = newStatus
                    )
                    if (result.isSuccess) {
                        book.value = book.value?.copy(interest = result.getOrThrow().interest)
                    }
                }
            }
        }
    }

}