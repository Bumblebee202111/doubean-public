package com.github.bumblebee202111.doubean.feature.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.login.LoginScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object LoginNavKey : NavKey

fun Navigator.navigateToLogin() = navigate(key = LoginNavKey)

fun EntryProviderScope<NavKey>.loginEntry(
    onPopBackStack: () -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Boolean,
) = entry<LoginNavKey> {
    LoginScreen(
        onBackClick = onPopBackStack,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl
    )
}