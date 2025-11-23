package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.model.ApiError
import com.github.bumblebee202111.doubean.model.AppResult
import com.github.bumblebee202111.doubean.model.auth.LoginResult
import com.github.bumblebee202111.doubean.ui.common.SnackbarManager
import com.github.bumblebee202111.doubean.ui.model.toUiMessage
import com.github.bumblebee202111.doubean.ui.util.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {

    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> get() = _isFormValid

    private val _loginResult = MutableStateFlow<LoginResult?>(null)

    val loginResult: StateFlow<LoginResult?> get() = _loginResult

    val sessionLoginResult = MutableStateFlow<Boolean?>(null)

    private val _solutionUri = MutableStateFlow<String?>(null)

    val solutionUri = _solutionUri.asStateFlow()

    fun triggerAutoImport() {
        viewModelScope.launch {
            authRepository.syncSessionFromDoubanPrefs()
        }
    }

    fun loginWithDoubanSession(sessionPref: String) {
        sessionLoginResult.value = null
        val loginResult = authRepository.loginWithDoubanSessionPref(sessionPref)
        sessionLoginResult.value = loginResult
        if (!loginResult) {
            snackbarManager.showMessage(R.string.error_import_failed.toUiMessage())
        }
    }

    fun login() {
        viewModelScope.launch {
            val sessionResult = authRepository.login(phoneNumber, password)
            _loginResult.value = when (sessionResult) {
                is AppResult.Error -> {
                    LoginResult.Error(appError = sessionResult.error)
                }

                is AppResult.Success -> {
                    LoginResult.Success
                }
            }
            if (sessionResult is AppResult.Error) {
                val error = sessionResult.error
                _solutionUri.value = (sessionResult.error as? ApiError)?.solutionUri
                snackbarManager.showMessage(error.asUiMessage())
            }
        }
    }

    fun updatePhoneNumber(phoneNumberInput: String) {
        this.phoneNumber = phoneNumberInput
        updateFormValid()
    }

    fun updatePassword(passwordInput: String) {
        this.password = passwordInput
        updateFormValid()
    }

    private fun updateFormValid() {
        _isFormValid.value = phoneNumber.length == 11 && password.length >= 6
    }

    fun clearSolutionUri() {
        _solutionUri.value = null
    }

}
