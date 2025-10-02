package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.groupsHomeScreen
import com.github.bumblebee202111.doubean.feature.statuses.navigation.statusesScreen
import com.github.bumblebee202111.doubean.feature.subjects.navigation.subjectsScreen
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.userProfileScreen
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@Composable
fun BottomNavHost(
    navController: NavHostController,
    startDestination: Any,
    navigateToSearch: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToGroupDetail: (groupId: String, defaultTabId: String?) -> Unit,
    navigateToTopic: (topicId: String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToSubjectInterests: (userId: String, subjectType: SubjectType) -> Unit,
    navigateToSearchSubjects: () -> Unit,
    navigateToRankList: (collectionId: String) -> Unit,
    navigateToMovie: (movieId: String) -> Unit,
    navigateToTv: (tvId: String) -> Unit,
    navigateToBook: (bookId: String) -> Unit,
    navigateToUri: (String) -> Boolean,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        statusesScreen(onAvatarClick = onAvatarClick)
        subjectsScreen(
            onAvatarClick = onAvatarClick,
            onSubjectStatusClick = navigateToSubjectInterests,
            onLoginClick = navigateToLogin,
            onSearchClick = navigateToSearchSubjects,
            onRankListClick = navigateToRankList,
            onMovieClick = navigateToMovie,
            onTvClick = navigateToTv,
            onBookClick = navigateToBook
        )
        groupsHomeScreen(
            onAvatarClick = onAvatarClick,
            onSearchClick = navigateToSearch,
            onNotificationsClick = navigateToNotifications,
            onGroupClick = navigateToGroupDetail,
            onTopicClick = navigateToTopic
        )
        userProfileScreen(
            onSettingsClick = navigateToSettings,
            onLoginClick = navigateToLogin,
            onStatItemUriClick = navigateToUri
        )
    }
}
