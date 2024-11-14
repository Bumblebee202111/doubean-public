package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.data.repository.BookRepository
import com.github.bumblebee202111.doubean.data.repository.UserSubjectRepository
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectCollection
import com.github.bumblebee202111.doubean.ui.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userSubjectRepository: UserSubjectRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _myBooksUiState: MutableStateFlow<MySubjectUiState> =
        MutableStateFlow(MySubjectUiState.Loading)
    val myBooksUiState = _myBooksUiState.asStateFlow()

    private val collectionItems: MutableStateFlow<List<SubjectWithInterest>> =
        MutableStateFlow(emptyList())

    private val collectionResult = flow {
        emit(bookRepository.getTop250BooksCollection().map(NetworkSubjectCollection::title))
    }

    private val collectionItemsResult =
        flow { emit(bookRepository.getTop250BooksCollectionItems()) }.onEach {
            collectionItems.value = it.getOrDefault(emptyList())
        }

    val isLoggedIn = authRepository.isLoggedIn()

    val booksUiState = combine(
        collectionResult,
        collectionItemsResult,
        isLoggedIn,
        collectionItems
    ) { collectionResult, collectionItemsResult, isLoggedIn, collectionItems ->
        when {
            collectionResult.isSuccess && collectionItemsResult.isSuccess -> {
                BooksUiState.Success(
                    title = collectionResult.getOrThrow(),
                    items = collectionItems,
                    isLoggedIn = isLoggedIn
                )
            }

            else ->
                BooksUiState.Error
        }
    }.stateInUi(BooksUiState.Loading)

    init {
        getMyBooks()
    }

    fun onMarkBook(book: SubjectWithInterest) {
        viewModelScope.launch {
            val result = userSubjectRepository.addSubjectToInterests(
                subject = book.subject,
                newStatus = SubjectInterest.Status.MARK_STATUS_MARK
            )
            if (result.isSuccess) {
                collectionItems.value = collectionItems.value.toMutableList().apply {
                    set(indexOf(book), result.getOrThrow())
                }
            }
        }
    }

    private fun getMyBooks() {
        viewModelScope.launch {
            authRepository.observeLoggedInUserId().onEach { userId ->
                _myBooksUiState.value = when (userId) {
                    null -> MySubjectUiState.NotLoggedIn
                    else -> {
                        val result = userSubjectRepository.getUserSubjects(
                            userId =
                            userId
                        )
                        when (result.isSuccess) {
                            true ->
                                MySubjectUiState.Success(
                                    userId = userId,
                                    mySubject = result.getOrThrow()
                                        .first { it.type == SubjectType.BOOK }
                                )

                            false -> MySubjectUiState.Error
                        }
                    }
                }
            }.collect()
        }
    }

}

sealed interface BooksUiState {
    data class Success(
        val title: String,
        val items: List<SubjectWithInterest>,
        val isLoggedIn: Boolean,
    ) : BooksUiState

    data object Error : BooksUiState
    data object Loading : BooksUiState
}