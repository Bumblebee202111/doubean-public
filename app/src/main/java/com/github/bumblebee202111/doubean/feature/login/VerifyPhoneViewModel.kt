package com.github.bumblebee202111.doubean.feature.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.feature.login.navigation.VerifyPhoneRoute
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.CaptchaSolution
import com.github.bumblebee202111.doubean.model.JCaptcha
import com.github.bumblebee202111.doubean.model.RequestPhoneCodeResult
import com.github.bumblebee202111.doubean.model.VerifyPhoneCodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId = savedStateHandle.toRoute<VerifyPhoneRoute>().userId

    private val _uiState = MutableStateFlow<VerifyPhoneMainUiState>(
        VerifyPhoneMainUiState.Active()
    )
    val uiState: StateFlow<VerifyPhoneMainUiState> = _uiState.asStateFlow()

    fun requestPhoneCode() {
        viewModelScope.launch {
            val currentState = (_uiState.value as? VerifyPhoneMainUiState.Active) ?: return@launch
            _uiState.update {
                VerifyPhoneMainUiState.Active(
                    isRequestingCode = true,
                    isVerifyingCode = false,
                    message = null,
                    error = null
                )
            }
            when (val result =
                authRepository.requestPhoneCode(
                    userId = userId,
                    captchaSolution = currentState.captchaSolution
                )) {
                is AppResult.Success -> handleRequestResult(result.data)
                is AppResult.Error -> handleRequestError(result.error)
            }
        }
    }

    fun updatePhoneCode(newCode: String) {
        val currentState = _uiState.value
        if (currentState is VerifyPhoneMainUiState.Active) {
            _uiState.value = currentState.copy(
                phoneCode = newCode,
                isPhoneCodeValid = newCode.length == 4
            )
        }
    }

    fun verifyPhoneCode() {
        viewModelScope.launch {
            // Invalid states are already prevented by UI
            val currentState = (_uiState.value as? VerifyPhoneMainUiState.Active) ?: return@launch
            val currentCode = currentState.phoneCode.takeIf {
                currentState.isPhoneCodeValid && currentState.isCodeSent
            } ?: return@launch
            val solution = currentState.captchaSolution
            if ((currentState.jCaptcha != null) != (solution != null)) {
                return@launch
            }

            _uiState.value = currentState.copy(
                isVerifyingCode = true,
                error = null
            )
            when (val result = authRepository.verifyPhoneCode(
                userId = userId,
                phoneCode = currentCode,
                captchaSolution = solution
            )) {
                is AppResult.Success -> handleVerificationSuccess(result.data)
                is AppResult.Error -> handleVerificationError(result.error)
            }
        }
    }

    fun clearMessage() {
        val current = _uiState.value as? VerifyPhoneMainUiState.Active ?: return
        _uiState.value = current.copy(message = null)
    }

    fun clearError() {
        val current = _uiState.value as? VerifyPhoneMainUiState.Active ?: return
        _uiState.value = current.copy(error = null)
    }

    fun verifyCaptcha(solution: CaptchaSolution) {
        val current = _uiState.value as? VerifyPhoneMainUiState.Active ?: return
        _uiState.update {
            current.copy(
                jCaptcha = null,
                captchaSolution = solution,
                error = null
            )
        }
        requestPhoneCode()
    }

    private fun handleRequestResult(result: RequestPhoneCodeResult) {
        _uiState.value = when (result) {
            is RequestPhoneCodeResult.Success -> VerifyPhoneMainUiState.Active(
                isCodeSent = true,
                isRequestingCode = false,
                message = result.description
            )

            is RequestPhoneCodeResult.Failed -> {
                VerifyPhoneMainUiState.Active(
                    jCaptcha = result.jCaptcha,
                    message = result.description
                )
            }
        }
    }

    private fun handleRequestError(error: AppError) {
        _uiState.value = VerifyPhoneMainUiState.Active(
            isRequestingCode = false,
            error = error
        )
    }

    private fun handleVerificationSuccess(result: VerifyPhoneCodeResult) {
        _uiState.value = when (result) {
            is VerifyPhoneCodeResult.Success -> {
                VerifyPhoneMainUiState.VerificationSuccess(
                    result.description
                )
            }

            is VerifyPhoneCodeResult.Failed -> {
                VerifyPhoneMainUiState.Active(
                    isVerifyingCode = false,
                    message = result.description
                )
            }
        }
    }

    private fun handleVerificationError(error: AppError) {
        _uiState.value = VerifyPhoneMainUiState.Active(
            isVerifyingCode = false,
            error = error
        )
    }

}

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
