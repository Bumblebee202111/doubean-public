package com.github.bumblebee202111.doubean.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiMessage {

    @Composable
    fun getString(): String

    data class Direct(val text: String) : UiMessage {
        @Composable
        override fun getString(): String = text
    }

    data class Resource(@StringRes val resId: Int, val formatArgs: List<Any> = emptyList()) :
        UiMessage {
        @Composable
        override fun getString(): String {
            return stringResource(resId, *formatArgs.toTypedArray())
        }
    }
}

fun String.toUiMessage() = UiMessage.Direct(this)
fun @receiver:StringRes Int.toUiMessage(vararg formatArgs: Any) =
    UiMessage.Resource(this, formatArgs.toList())