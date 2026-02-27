package com.github.bumblebee202111.doubean.feature.subjects.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.subjects.SubjectsScreen
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import kotlinx.serialization.Serializable

@Serializable
data object SubjectsNavKey : NavKey

fun EntryProviderScope<NavKey>.subjectsEntry(
    onAvatarClick: () -> Unit,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    entry<SubjectsNavKey> {
        SubjectsScreen(
            onAvatarClick = onAvatarClick,
            onSubjectStatusClick = onSubjectStatusClick,
            onLoginClick = onLoginClick,
            onSearchClick = onSearchClick,
            onRankListClick = onRankListClick,
            onMovieClick = onMovieClick,
            onTvClick = onTvClick,
            onBookClick = onBookClick
        )
    }
}