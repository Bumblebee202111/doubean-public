package com.github.bumblebee202111.doubean.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import com.github.bumblebee202111.doubean.ui.BottomNavScreen
import kotlinx.serialization.Serializable

@Serializable
data object BottomNavRoute

fun NavGraphBuilder.bottomNavScreen(
    startWithGroups: Boolean,
    navigateToSearch: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToGroup: (groupId: String, defaultTabId: String?) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToSubjectInterests: (userId: String, subjectType: SubjectType) -> Unit,
    navigateToSearchSubjects: (type: SubjectsSearchType) -> Unit,
    navigateToMovie: (movieId: String) -> Unit,
    navigateToTv: (tvId: String) -> Unit,
    navigateToBook: (bookId: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
): Unit = composable<BottomNavRoute> {
    BottomNavScreen(
        startWithGroups = startWithGroups,
        navigateToSearch = navigateToSearch,
        navigateToNotifications = navigateToNotifications,
        navigateToSettings = navigateToSettings,
        navigateToGroup = navigateToGroup,
        navigateToTopic = navigateToTopic,
        navigateToLogin = navigateToLogin,
        navigateToSubjectInterests = navigateToSubjectInterests,
        navigateToSearchSubjects = navigateToSearchSubjects,
        navigateToMovie = navigateToMovie,
        navigateToTv = navigateToTv,
        navigateToBook = navigateToBook,
        onShowSnackbar = onShowSnackbar
    )
}