package com.github.bumblebee202111.doubean.feature.subjects.book.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.subjects.book.BookScreen
import kotlinx.serialization.Serializable

@Serializable
data class BookRoute(val bookId: String)

fun NavGraphBuilder.bookScreen(onBackClick: () -> Unit, onLoginClick: () -> Unit) {
    composable<BookRoute> {
        BookScreen(onBackClick = onBackClick, onLoginClick = onLoginClick)
    }
}

fun NavController.navigateToBook(bookId: String) = navigate(BookRoute(bookId))
