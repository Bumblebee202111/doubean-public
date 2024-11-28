package com.github.bumblebee202111.doubean.feature.subjects.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.BookRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.book.navigation.BookRoute
import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
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
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val bookId = savedStateHandle.toRoute<BookRoute>().bookId
    private val book = MutableStateFlow<SubjectWithInterest<Book>?>(null)
    private val bookResult = flow {
        emit(bookRepository.getBook(bookId))
    }.onEach {
        book.value = it.getOrNull()
    }
    val bookUiState = combine(book, bookResult) { book, bookResult ->
        if (bookResult.isSuccess) {
            BookUiState.Success(book!!)
        } else {
            BookUiState.Error
        }
    }.stateInUi(BookUiState.Loading)

    fun onUpdateStatus(
        subjectWithInterest: SubjectWithInterest<Book>,
        status: SubjectInterest.Status,
    ) {
        when (status) {
            SubjectInterest.Status.MARK_STATUS_UNMARK -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.unmarkSubject(subjectWithInterest.subject)
                    if (result.isSuccess) {
                        book.value = book.value?.let {
                            SubjectWithInterest(subject = it.subject)
                        }
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    val result = userSubjectRepository.addSubjectToInterests(
                        subject = subjectWithInterest.subject,
                        newStatus = status
                    )
                    if (result.isSuccess) {
                        book.value = result.getOrThrow()
                    }
                }
            }
        }
    }

}