package com.github.bumblebee202111.doubean.feature.subjects.interests.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.interests.InterestsScreen
import com.github.bumblebee202111.doubean.feature.subjects.interests.InterestsViewModel
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class InterestsNavKey(
    val userId: String,
    val subjectType: SubjectType,
) : NavKey

fun Navigator.navigateToInterests(userId: String, subjectType: SubjectType) {
    navigate(InterestsNavKey(userId, subjectType))
}

fun EntryProviderScope<NavKey>.interestsEntry(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    entry<InterestsNavKey> { key ->
        InterestsScreen(
            onBackClick = onBackClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick,
            viewModel = hiltViewModel<InterestsViewModel, InterestsViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(userId = key.userId, subjectType = key.subjectType)
                }
            )
        )
    }
}