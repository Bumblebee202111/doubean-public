package com.github.bumblebee202111.doubean.feature.login.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.bumblebee202111.doubean.feature.login.VerifyPhoneScreen
import com.github.bumblebee202111.doubean.feature.login.VerifyPhoneViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyPhoneNavKey(
    @SerialName("user_id")
    val userId: String,
) : NavKey

fun EntryProviderScope<NavKey>.verifyPhoneEntry(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) = entry<VerifyPhoneNavKey> { key ->
    VerifyPhoneScreen(
        onBackClick = onBackClick,
        onSuccess = onSuccess,
        viewModel = hiltViewModel<VerifyPhoneViewModel, VerifyPhoneViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(userId = key.userId)
            }
        )
    )
}