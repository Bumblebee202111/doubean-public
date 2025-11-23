package com.github.bumblebee202111.doubean.model

import java.io.IOException


sealed interface AppError {
    val originalError: Throwable?
}

sealed interface NetworkError : AppError
data class ApiError(
    val status: Int,
    val code: Int,
    val message: String?,
    val request: String,
    val solutionUri: String? = null,
    override val originalError: Throwable?,
) : NetworkError {
    companion object {
        const val ACCESS_TOKEN_HAS_EXPIRED = 106
        const val ACCESS_TOKEN_HAS_EXPIRED_SINCE_PASSWORD_CHANGED = 123
        const val ACCESS_TOKEN_HAS_NOT_EXPIRED = 124
        const val ACCESS_TOKEN_IS_MISSING = 102
        const val APIKEY_IS_BLOCKED = 105
        const val CLIENT_SECRET_MISMATCH = 116
        const val HAS_BAN_WORD = 1004
        const val IMAGE_TOO_LARGE = 1003
        const val IMAGE_UNKNOWN = 1008
        const val IMAGE_WRONG_FORMAT = 1009
        const val INPUT_TOO_SHORT = 1005
        const val INVALID_ACCESS_TOKEN = 103
        const val INVALID_APIKEY = 104
        const val INVALID_AUTHORIZATION_CODE = 118
        const val INVALID_CREDENTIAL = 108
        const val INVALID_CREDENTIAL2 = 109
        const val INVALID_REFRESH_TOKEN = 119
        const val INVALID_REQUEST_METHOD = 101
        const val INVALID_REQUEST_SCHEME = 100
        const val INVALID_REQUEST_URI = 107
        const val INVALID_USER = 121
        const val MISSING_ARGS = 1002
        const val NEED_CAPTCHA = 1007
        const val NEED_PERMISSION = 1000
        const val NOT_TRIAL_USER = 110
        const val RATE_LIMIT_EXCEEDED = 111
        const val RATE_LIMIT_EXCEEDED2 = 112
        const val REDIRECT_URI_MISMATCH = 117
        const val REQUIRED_PARAMETER_IS_MISSING = 113
        const val TARGET_NOT_FOUND = 1006
        const val UNKNOWN = 999
        const val UNKNOWN_V2_ERROR = 1999
        const val UNSUPPORTED_GRANT_TYPE = 114
        const val UNSUPPORTED_RESPONSE_TYPE = 115
        const val URI_NOT_FOUND = 1001
        const val USERNAME_PASSWORD_MISMATCH = 120
        const val USER_HAS_BLOCKED = 122
    }
}

data class ConnectionError(override val originalError: Throwable?) : NetworkError

data class GenericError(override val originalError: Throwable?) : AppError

class AppErrorException(val appError: AppError) : IOException()