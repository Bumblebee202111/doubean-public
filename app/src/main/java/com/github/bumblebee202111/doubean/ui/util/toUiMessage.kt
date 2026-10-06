package com.github.bumblebee202111.doubean.ui.util

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.AppErrorException
import com.github.bumblebee202111.doubean.ui.model.UiMessage
import com.github.bumblebee202111.doubean.ui.model.toUiMessage

@Composable
fun LoadState.Error.toUiMessage(): UiMessage {
    val error = this.error
    return when (error) {
        is AppErrorException -> error.appError.asUiMessage()
        else -> error.localizedMessage?.toUiMessage() ?: R.string.error_unknown.toUiMessage()
    }
}