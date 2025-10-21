package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.bumblebee202111.doubean.feature.app.navigation.bottomNavScreen
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation.createdDouListsScreen
import com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation.douListScreen
import com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation.navigateToDouList
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
import com.github.bumblebee202111.doubean.feature.mydoulists.navigation.myDouListsScreen
import com.github.bumblebee202111.doubean.feature.mydoulists.navigation.navigateToMyDouLists
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
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.navigateToUserProfile
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.userProfileScreen
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager

@Composable
fun MainNavHost(
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    startDestination: Any,
    startWithGroups: Boolean,
    onActiveTabAppearanceNeeded: (useLightIcons: Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        val navigateToUriBasic = { uriString: String ->
            navController.tryNavigateToUri(uriString, snackbarManager)
        }

        bottomNavScreen(
            startWithGroups = startWithGroups,
            onActiveTabAppearanceNeeded = onActiveTabAppearanceNeeded,
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
            navigateToBook = navController::navigateToBook,
            navigateToUserProfile = navController::navigateToUserProfile,
            navigateToMyDouLists = navController::navigateToMyDouLists,
            navigateToUri = navigateToUriBasic
        )
        groupsSearchScreen(
            onGroupClick = navController::navigateToGroup,
            onBackClick = navController::navigateUp
        )
        notificationsScreen(
            onBackClick = navController::navigateUp,
            onTopicClick = navController::navigateToTopic,
            onGroupClick = { groupId ->
                navController.navigateToGroup(groupId)
            },
            onSettingsClick = navController::navigateToSettings
        )
        groupDetailScreen(
            onBackClick = navController::navigateUp,
            onTopicClick = navController::navigateToTopic,
            onUserClick = navController::navigateToUserProfile
        )
        topicScreen(
            onBackClick = navController::navigateUp,
            onWebViewClick = navController::navigateToWebView,
            onGroupClick = navController::navigateToGroup,
            onReshareStatusesClick = navController::navigateToReshareStatuses,
            onUserClick = navController::navigateToUserProfile,
            onImageClick = navController::navigateToImage,
            onOpenDeepLinkUrl = { uriString: String, showSnackbarOnFailure: Boolean ->
                navController.tryNavigateToUri(
                    uriString = uriString,
                    snackbarManager = snackbarManager,
                    showSnackbarOnFailure = showSnackbarOnFailure
                )
            }
        )
        reshareStatusesScreen(
            onBackClick = navController::navigateUp,
            onUserClick = navController::navigateToUserProfile
        )
        webViewScreen(onBackClick = navController::navigateUp)
        loginScreen(
            onSaveIsLoginSuccessSuccessfulChange = {
                navController.previousBackStackEntry!!.savedStateHandle[LOGIN_SUCCESSFUL] = it
            },
            onPopBackStack = navController::popBackStack,
            onOpenDeepLinkUrl = navigateToUriBasic
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
        userProfileScreen(
            onBackClick = navController::navigateUp,
            onStatItemUriClick = navigateToUriBasic
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
            onImageClick = navController::navigateToImage,
            onUserClick = navController::navigateToUserProfile,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook
        )
        tvScreen(
            onBackClick = navController::navigateUp,
            onLoginClick = navController::navigateToLogin,
            onImageClick = navController::navigateToImage,
            onUserClick = navController::navigateToUserProfile,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook
        )
        bookScreen(
            onBackClick = navController::navigateUp,
            onLoginClick = navController::navigateToLogin,
            onImageClick = navController::navigateToImage,
            onUserClick = navController::navigateToUserProfile,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook
        )
        rankListScreen(
            onBackClick = navController::navigateUp,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onBookClick = navController::navigateToBook
        )
        createdDouListsScreen(
            onBackClick = navController::navigateUp,
            onDouListClick = navController::navigateToDouList
        )
        douListScreen(
            onBackClick = navController::navigateUp,
            onTopicClick = navController::navigateToTopic,
            onBookClick = navController::navigateToBook,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onUserClick = navController::navigateToUserProfile,
            onImageClick = navController::navigateToImage
        )
        myDouListsScreen(
            onBackClick = navController::navigateUp,
            onTopicClick = navController::navigateToTopic,
            onBookClick = navController::navigateToBook,
            onMovieClick = navController::navigateToMovie,
            onTvClick = navController::navigateToTv,
            onUserClick = navController::navigateToUserProfile,
            onImageClick = navController::navigateToImage,
            onDouListClick = navController::navigateToDouList
        )
    }
}