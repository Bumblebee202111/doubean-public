package com.github.bumblebee202111.doubean.navigation

import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest

fun NavController.navigateWithDeepLinkUrl(url: String) {
    val request =
        NavDeepLinkRequest.Builder.fromUri(url.toUri()).build()
    navigate(request)
}