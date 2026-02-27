package com.github.bumblebee202111.doubean.feature.groups.webView.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.groups.webView.WebViewScreen
import com.github.bumblebee202111.doubean.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class WebViewNavKey(
    val url: String,
) : NavKey

fun Navigator.navigateToWebView(url: String) = navigate(key = WebViewNavKey(url))

fun EntryProviderScope<NavKey>.webViewEntry(onBackClick: () -> Unit) = entry<WebViewNavKey> { key ->
    WebViewScreen(
        key.url,
        onBackClick = onBackClick,
    )
}