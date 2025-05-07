package com.github.bumblebee202111.doubean.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.github.bumblebee202111.doubean.feature.login.VerifyPhoneScreen
import kotlinx.serialization.Serializable

@Serializable
data class VerifyPhoneRoute(
    val userId: String,
)

fun NavGraphBuilder.verifyPhoneScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) = composable<VerifyPhoneRoute>(
    deepLinks =
    listOf(
        navDeepLink {
            uriPattern = "douban://douban.com/account/login_verify_phone?user_id={userId}"
        }
    )

) {
    VerifyPhoneScreen(
        onBackClick = onBackClick,
        onSuccess = onSuccess
    )
}