package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.BookRepository
import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.network.model.NetworkSubjectCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {
    init {
        getTop250MoviesCollectionWithItems()
    }

    private val _booksUiState: MutableStateFlow<BooksUiState> =
        MutableStateFlow(BooksUiState.Loading)
    val moviesUiState = _booksUiState.asStateFlow()
    private fun getTop250MoviesCollectionWithItems() {
        viewModelScope.launch {
            val collectionResult =
                async {
                    bookRepository.getTop250BooksCollection().map(NetworkSubjectCollection::title)
                }.await()
            val collectionItemsResult =
                async {
                    bookRepository.getTop250BooksCollectionItems()
                }.await()

            _booksUiState.value = when {
                collectionResult.isSuccess && collectionItemsResult.isSuccess -> {
                    BooksUiState.Success(
                        title = collectionResult.getOrThrow(),
                        items = collectionItemsResult.getOrThrow()
                    )
                }

                else -> BooksUiState.Error
            }
        }
    }
}

sealed interface BooksUiState {
    data class Success(
        val title: String,
        val items: List<Book>,
    ) : BooksUiState

    data object Error : BooksUiState
    data object Loading : BooksUiState
}