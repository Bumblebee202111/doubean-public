package com.github.bumblebee202111.doubean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailRoute
import com.github.bumblebee202111.doubean.feature.groups.groupDetail.GroupDetailScreen
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.ReshareStatusesRoute
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.ReshareStatusesScreen
import com.github.bumblebee202111.doubean.feature.groups.search.GroupsSearchRoute
import com.github.bumblebee202111.doubean.feature.groups.topicdetail.TopicDetailRoute
import com.github.bumblebee202111.doubean.feature.groups.topicdetail.TopicDetailScreen
import com.github.bumblebee202111.doubean.feature.groups.webView.WebViewRoute
import com.github.bumblebee202111.doubean.feature.image.ImageRoute
import com.github.bumblebee202111.doubean.feature.image.ImageScreen
import com.github.bumblebee202111.doubean.feature.login.LOGIN_SUCCESSFUL
import com.github.bumblebee202111.doubean.feature.login.LoginRoute
import com.github.bumblebee202111.doubean.feature.login.LoginScreen
import com.github.bumblebee202111.doubean.feature.login.VerifyPhoneRoute
import com.github.bumblebee202111.doubean.feature.login.VerifyPhoneScreen
import com.github.bumblebee202111.doubean.feature.settings.GroupDefaultNotificationsPreferencesSettingsRoute
import com.github.bumblebee202111.doubean.feature.settings.GroupDefaultNotificationsPreferencesSettingsScreen
import com.github.bumblebee202111.doubean.feature.settings.SettingsRoute
import com.github.bumblebee202111.doubean.feature.settings.SettingsScreen
import com.github.bumblebee202111.doubean.ui.BottomNavRoute
import com.github.bumblebee202111.doubean.ui.BottomNavScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    onShowSnackbar: suspend (String) -> Unit,
    startDestination: Any,
    startWithGroups: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<BottomNavRoute> {
            BottomNavScreen(
                startWithGroups = startWithGroups,
                navigateToSearch = {
                    navController.navigate(GroupsSearchRoute)
                },
                navigateToSettings = {
                    navController.navigate(SettingsRoute)
                },
                navigateToGroup = { groupId, defaultTabId ->
                    navController.navigate(GroupDetailRoute(groupId, defaultTabId))
                },
                navigateToTopic = {
                    navController.navigate(TopicDetailRoute(it))
                },
                navigateToLogin = {
                    navController.navigate(LoginRoute)
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<GroupsSearchRoute> {
            GroupsSearchRoute(onGroupClick = {
                navController.navigate(GroupDetailRoute(it))
            })
        }

        composable<GroupDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https:
                },
                navDeepLink {
                    uriPattern = "https:
                },
            )
        ) {
            navController.previousBackStackEntry
            GroupDetailScreen(
                onBackClick = navController::navigateUp,
                onTopicClick = { navController.navigate(TopicDetailRoute(it)) },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<TopicDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https:
                }
            )
        ) {
            TopicDetailScreen(
                onBackClick = navController::navigateUp,
                onWebViewClick = {
                    navController.navigate(WebViewRoute(it))
                },
                onGroupClick = { groupId, tabId ->
                    navController.navigate(GroupDetailRoute(groupId, tabId))
                },
                onReshareStatusesClick = {
                    navController.navigate(ReshareStatusesRoute(it))
                },
                onImageClick = { navController.navigate(ImageRoute(it)) },
                onOpenDeepLinkUrl = { url ->
                    val request =
                        NavDeepLinkRequest.Builder.fromUri(url.toUri()).build()
                    navController.navigate(request)
                },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<ReshareStatusesRoute> {
            ReshareStatusesScreen(
                onBackClick = navController::navigateUp,
            )
        }

        composable<WebViewRoute> {
            WebViewRoute(
                it.toRoute<WebViewRoute>().url,
                onBackClick = navController::navigateUp,
            )
        }

        composable<LoginRoute> {
            LoginScreen(onSaveIsLoginSuccessSuccessfulChange = {
                navController.previousBackStackEntry!!.savedStateHandle[LOGIN_SUCCESSFUL] = it
            }, onPopBackStack = {
                navController.popBackStack()
            }, onOpenDeepLinkUrl = { url ->
                val request =
                    NavDeepLinkRequest.Builder.fromUri(url.toUri()).build()
                navController.navigate(request)
            },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<VerifyPhoneRoute>(
            deepLinks =
            listOf(
                navDeepLink {
                    uriPattern = "douban:
                }
            )

        ) {
            VerifyPhoneScreen(
                onPopBackStack = { navController.popBackStack(LoginRoute, false) },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onBackClick = navController::navigateUp,
                onGroupDefaultNotificationsPreferencesSettingsClick = {
                    navController.navigate(GroupDefaultNotificationsPreferencesSettingsRoute)
                }
            )
        }

        composable<GroupDefaultNotificationsPreferencesSettingsRoute> {
            GroupDefaultNotificationsPreferencesSettingsScreen(
                onBackClick = navController::navigateUp,
            )
        }

        composable<ImageRoute> {
            ImageScreen(
                navigateUp = navController::navigateUp,
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}