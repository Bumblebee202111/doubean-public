package com.github.bumblebee202111.doubean.ui.util

import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.ApiError
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.ConnectionError
import com.github.bumblebee202111.doubean.model.GenericError
import com.github.bumblebee202111.doubean.ui.model.UiMessage
import com.github.bumblebee202111.doubean.ui.model.toUiMessage

fun AppError.asUiMessage(): UiMessage {
    return when (this) {
        is ApiError -> {
            if (message != null && code != ApiError.UNKNOWN) {
                return message.toUiMessage()
            }
            when (code) {

                ApiError.INVALID_ACCESS_TOKEN,
                ApiError.ACCESS_TOKEN_HAS_EXPIRED,
                    -> {
                    return R.string.error_access_token_invalid.toUiMessage(code)

                }

                ApiError.UNKNOWN -> R.string.error_unknown.toUiMessage(code)
                else -> {
                    val stringResId = when (code) {

                        ApiError.USERNAME_PASSWORD_MISMATCH -> R.string.error_username_password_mismatch


                        ApiError.HAS_BAN_WORD -> R.string.error_ban_word


                        ApiError.USER_HAS_BLOCKED -> R.string.error_blocked_by_user
                        ApiError.INVALID_USER -> R.string.error_invalid_user


                        else -> null
                    }
                    if (stringResId != null) {
                        return stringResId.toUiMessage()
                    }
                }
            }

            return when {
                status in 400..499 -> R.string.error_client.toUiMessage(code)
                status >= 500 -> R.string.error_server.toUiMessage(code)
                else -> R.string.error_unknown.toUiMessage(code)
            }
        }

        is ConnectionError -> R.string.error_network.toUiMessage()

        is GenericError -> originalError?.message.takeUnless {
            it.isNullOrBlank()
        }?.toUiMessage() ?: R.string.error_unknown.toUiMessage()

    }

}