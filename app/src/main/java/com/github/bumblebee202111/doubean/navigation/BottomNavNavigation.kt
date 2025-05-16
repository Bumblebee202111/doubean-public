package com.github.bumblebee202111.doubean.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
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
    navigateToSearchSubjects: () -> Unit,
    navigateToRankList: (collectionId: String) -> Unit,
    navigateToMovie: (movieId: String) -> Unit,
    navigateToTv: (tvId: String) -> Unit,
    navigateToBook: (bookId: String) -> Unit,
    navigateToUserProfile: (userId: String) -> Unit,
    navigateToUri: (String) -> Boolean,
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
        navigateToRankList = navigateToRankList,
        navigateToMovie = navigateToMovie,
        navigateToTv = navigateToTv,
        navigateToBook = navigateToBook,
        navigateToUserProfile = navigateToUserProfile,
        navigateToUri = navigateToUri
    )
}