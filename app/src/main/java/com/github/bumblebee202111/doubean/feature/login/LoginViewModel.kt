package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.model.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> get() = _isFormValid

    private val _loginResult = MutableStateFlow<LoginResult?>(null)

    val loginResult: StateFlow<LoginResult?> get() = _loginResult

    val sessionLoginResult = MutableStateFlow<Boolean?>(null)
    fun loginWithDoubanSession(sessionPref: String) {
        sessionLoginResult.value = authRepository.loginWithDoubanSessionPref(sessionPref)
    }

    fun login() {
        viewModelScope.launch {
            _loginResult.value = authRepository.login(phoneNumber, password)
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

}
