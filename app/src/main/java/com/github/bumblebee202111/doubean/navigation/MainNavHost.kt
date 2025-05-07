package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.groupDetailScreen
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.navigateToGroup
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.navigateToReshareStatuses
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.reshareStatusesScreen
import com.github.bumblebee202111.doubean.feature.groups.search.navigation.groupsSearchScreen
import com.github.bumblebee202111.doubean.feature.groups.search.navigation.navigateToSearch
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.navigateToTopic
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.topicScreen
import com.github.bumblebee202111.doubean.feature.groups.webView.navigation.navigateToWebView
import com.github.bumblebee202111.doubean.feature.groups.webView.navigation.webViewScreen
import com.github.bumblebee202111.doubean.feature.image.navigation.imageScreen
import com.github.bumblebee202111.doubean.feature.image.navigation.navigateToImage
import com.github.bumblebee202111.doubean.feature.login.LOGIN_SUCCESSFUL
import com.github.bumblebee202111.doubean.feature.login.navigation.LoginRoute
import com.github.bumblebee202111.doubean.feature.login.navigation.loginScreen
import com.github.bumblebee202111.doubean.feature.login.navigation.navigateToLogin
import com.github.bumblebee202111.doubean.feature.login.navigation.verifyPhoneScreen
import com.github.bumblebee202111.doubean.feature.notifications.navigation.navigateToNotifications
import com.github.bumblebee202111.doubean.feature.notifications.navigation.notificationsScreen
import com.github.bumblebee202111.doubean.feature.settings.navigation.groupDefaultNotificationsPreferencesSettingsScreen
import com.github.bumblebee202111.doubean.feature.settings.navigation.navigateToGroupDefaultNotificationsPreferencesSettings
import com.github.bumblebee202111.doubean.feature.settings.navigation.navigateToSettings
import com.github.bumblebee202111.doubean.feature.settings.navigation.settingsScreen
import com.github.bumblebee202111.doubean.feature.subjects.book.navigation.bookScreen
import com.github.bumblebee202111.doubean.feature.subjects.book.navigation.navigateToBook
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.interestsScreen
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.navigateToInterests
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.movieScreen
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.navigateToMovie
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.navigateToRankList
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.rankListScreen
import com.github.bumblebee202111.doubean.feature.subjects.search.navigation.navigateToSearchSubjects
import com.github.bumblebee202111.doubean.feature.subjects.search.navigation.searchSubjectsScreen
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.navigateToTv
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.tvScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Any,
    startWithGroups: Boolean,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        bottomNavScreen(
            startWithGroups = startWithGroups,
            navigateToSearch = navController::navigateToSearch,
            navigateToNotifications = navController::navigateToNotifications,
            navigateToSettings = navController::navigateToSettings,
            navigateToGroup = navController::navigateToGroup,
            navigateToTopic = navController::navigateToTopic,
            navigateToLogin = navController::navigateToLogin,
            navigateToSubjectInterests = navController::navigateToInterests,
            navigateToSearchSubjects = navController::navigateToSearchSubjects,
            navigateToRankList = navController::navigateToRankList,
            navigateToMovie = navController::navigateToMovie,
            navigateToTv = navController::navigateToTv,
            navigateToBook = navController::navigateToBook
        )
        groupsSearchScreen(
            onGroupClick = navController::navigateToGroup,
            onBackClick = navController::navigateUp
        )
        notificationsScreen(
            onBackClick = navController::navigateUp,
            onTopicClick = navController::navigateToTopic,
            onSettingsClick = navController::navigateToSettings
        )
        groupDetailScreen(
            onBackClick = navController::navigateUp,
            onTopicClick = navController::navigateToTopic
        )
        topicScreen(
            onBackClick = navController::navigateUp,
            onWebViewClick = navController::navigateToWebView,
            onGroupClick = navController::navigateToGroup,
            onReshareStatusesClick = navController::navigateToReshareStatuses,
            onImageClick = navController::navigateToImage,
            onOpenDeepLinkUrl = navController::navigateWithDeepLinkUrl
        )
        reshareStatusesScreen(onBackClick = navController::navigateUp)
        webViewScreen(onBackClick = navController::navigateUp)
        loginScreen(
            onSaveIsLoginSuccessSuccessfulChange = {
                navController.previousBackStackEntry!!.savedStateHandle[LOGIN_SUCCESSFUL] = it
            },
            onPopBackStack = navController::popBackStack,
            onOpenDeepLinkUrl = navController::navigateWithDeepLinkUrl
        )
        verifyPhoneScreen(
            onBackClick = navController::navigateUp,
            onSuccess = { navController.popBackStack(LoginRoute, true) }
        )
        settingsScreen(
            onBackClick = navController::navigateUp,
            onGroupDefaultNotificationsPreferencesSettingsClick =
            navController::navigateToGroupDefaultNotificationsPreferencesSettings,
            onLoginClick = navController::navigateToLogin
        )
        groupDefaultNotificationsPreferencesSettingsScreen(
            onBackClick = navController::navigateUp
        )
        imageScreen(
            navigateUp = navController::navigateUp
        )
        interestsScreen(
            onBackClick = navController::navigateUp,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook
        )
        searchSubjectsScreen(
            onBackClick = navController::navigateUp,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook,
        )
        movieScreen(
            onBackClick = navController::navigateUp,
            onLoginClick = navController::navigateToLogin,
            onImageClick = navController::navigateToImage
        )
        tvScreen(
            onBackClick = navController::navigateUp,
            onLoginClick = navController::navigateToLogin,
            onImageClick = navController::navigateToImage
        )
        bookScreen(
            onBackClick = navController::navigateUp,
            onLoginClick = navController::navigateToLogin,
            onImageClick = navController::navigateToImage
        )
        rankListScreen(
            onBackClick = navController::navigateUp,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook
        )
    }
}