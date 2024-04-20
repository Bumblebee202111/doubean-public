package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.model.VerifyPhoneRequestCodeResult
import com.github.bumblebee202111.doubean.model.VerifyPhoneVerifyCodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId = VerifyPhoneFragmentArgs.fromSavedStateHandle(savedStateHandle).userId
    var requestCodeResult: MutableStateFlow<VerifyPhoneRequestCodeResult?> = MutableStateFlow(null)
        private set

    var verifyCodeResult: MutableStateFlow<VerifyPhoneVerifyCodeResult?> = MutableStateFlow(null)
        private set
    var code by mutableStateOf("")
        private set

    var isCodeValid by mutableStateOf(false)
        private set

    fun requestSendCode() {
        viewModelScope.launch {
            requestCodeResult.value = authRepository.verifyPhoneRequestSendCode(userId)
        }
    }

    fun submitCode() {
        viewModelScope.launch {
            verifyCodeResult.value = authRepository.verifyPhoneVerifyCode(userId, code)

        }
    }

    fun updateCodeInput(codeInput: String) {
        code = codeInput
        isCodeValid = codeInput.length == 4
    }

}

sealed class VerifyPhoneState {
    data object NotStarted : VerifyPhoneState()

}