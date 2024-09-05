package com.github.bumblebee202111.doubean.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.bumblebee202111.doubean.feature.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavController.navigateToLogin() = navigate(route = LoginRoute)

fun NavGraphBuilder.loginScreen(
    onSaveIsLoginSuccessSuccessfulChange: (Boolean) -> Unit,
    onPopBackStack: () -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) = composable<LoginRoute> {
    LoginScreen(
        onSaveIsLoginSuccessSuccessfulChange = onSaveIsLoginSuccessSuccessfulChange,
        onPopBackStack = onPopBackStack,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onShowSnackbar = onShowSnackbar
    )
}