package com.github.bumblebee202111.doubean.feature.subjects.book.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.book.BookScreen
import com.github.bumblebee202111.doubean.feature.subjects.book.BookViewModel
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class BookNavKey(val bookId: String) : NavKey

fun EntryProviderScope<NavKey>.bookEntry(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
) {
    entry<BookNavKey> { key ->
        BookScreen(
            onBackClick = onBackClick,
            onLoginClick = onLoginClick,
            onImageClick = onImageClick,
            onUserClick = onUserClick,
            onSubjectClick = onSubjectClick,
            viewModel = hiltViewModel<BookViewModel, BookViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.bookId)
                }
            )
        )
    }
}

fun Navigator.navigateToBook(bookId: String) = navigate(BookNavKey(bookId))
