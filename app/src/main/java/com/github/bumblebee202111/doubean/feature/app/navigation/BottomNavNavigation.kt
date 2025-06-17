package com.github.bumblebee202111.doubean.feature.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.app.BottomNavScreen
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import kotlinx.serialization.Serializable

@Serializable
data object BottomNavRoute

fun NavGraphBuilder.bottomNavScreen(
    startWithGroups: Boolean,
    onActiveTabAppearanceNeeded: (useLightIcons: Boolean?) -> Unit,
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
    navigateToMyDouLists: () -> Unit,
    navigateToUri: (String) -> Boolean,
): Unit = composable<BottomNavRoute> {
    BottomNavScreen(
        onActiveTabAppearanceNeeded = onActiveTabAppearanceNeeded,
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
        navigateToMyDouLists = navigateToMyDouLists,
        navigateToUri = navigateToUri
    )
}