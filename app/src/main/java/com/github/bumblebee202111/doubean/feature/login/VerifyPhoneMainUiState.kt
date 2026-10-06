package com.github.bumblebee202111.doubean.feature.login

import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.auth.CaptchaSolution
import com.github.bumblebee202111.doubean.model.auth.JCaptcha

sealed interface VerifyPhoneMainUiState {
    data class Active(
        val phoneCode: String = "",
        val isPhoneCodeValid: Boolean = false,
        val isRequestingCode: Boolean = false,
        val isVerifyingCode: Boolean = false,
        val isCodeSent: Boolean = false,
        val jCaptcha: JCaptcha? = null,
        val captchaSolution: CaptchaSolution? = null,
        val message: String? = null,
        val error: AppError? = null,
        val countdownSeconds: Int = 0,
    ) : VerifyPhoneMainUiState {
        val canVerify: Boolean
            get() = isCodeSent &&
                    isPhoneCodeValid &&
                    (jCaptcha == null || captchaSolution != null) &&
                    !isVerifyingCode
    }

    data class VerificationSuccess(
        val message: String,
    ) : VerifyPhoneMainUiState
}