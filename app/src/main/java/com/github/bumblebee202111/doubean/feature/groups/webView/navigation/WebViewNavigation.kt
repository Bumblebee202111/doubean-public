package com.github.bumblebee202111.doubean.feature.groups.webView.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.feature.groups.webView.WebViewScreen
import kotlinx.serialization.Serializable

@Serializable
data class WebViewRoute(
    val url: String,
)

fun NavController.navigateToWebView(url: String) = navigate(route = WebViewRoute(url))

fun NavGraphBuilder.webViewScreen(onBackClick: () -> Unit) = composable<WebViewRoute> {
    WebViewScreen(
        it.toRoute<WebViewRoute>().url,
        onBackClick = onBackClick,
    )
}