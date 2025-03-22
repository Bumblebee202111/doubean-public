package com.github.bumblebee202111.doubean.feature.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bumblebee202111.doubean.data.repository.AuthRepository
import com.github.bumblebee202111.doubean.feature.login.navigation.VerifyPhoneRoute
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

    private val userId = savedStateHandle.toRoute<VerifyPhoneRoute>().userId
    var requestCodeResult: MutableStateFlow<VerifyPhoneRequestCodeResult?> = MutableStateFlow(null)
        private set

    var verifyCodeResult: MutableStateFlow<VerifyPhoneVerifyCodeResult?> = MutableStateFlow(null)
        private set
    var code by mutableStateOf("")
        private set

    var isCodeValid by mutableStateOf(false)
        private set

    var displaySuccess by mutableStateOf(false)

    var errorMessage by mutableStateOf<String?>(null)

    fun requestSendCode() {
        viewModelScope.launch {
            val result = authRepository.verifyPhoneRequestSendCode(userId)
            Log.d("requestSendCode", result.toString())
            requestCodeResult.value = result
        }
    }

    fun submitCode() {
        viewModelScope.launch {
            val result = authRepository.verifyPhoneVerifyCode(userId, code)
            verifyCodeResult.value = result
            when (result) {
                VerifyPhoneVerifyCodeResult.Success -> {
                    displaySuccess = true
                    errorMessage = null

                }
                is VerifyPhoneVerifyCodeResult.Error -> {
                    displaySuccess = false
                    errorMessage = result.message
                }
            }
        }
    }

    fun updateCodeInput(codeInput: String) {
        code = codeInput
        isCodeValid = codeInput.length == 4
    }

    fun clearDisplaySuccessState() {
        displaySuccess = false
    }

    fun clearDisplayErrorState() {
        errorMessage = null
    }

}

sealed class VerifyPhoneState {
    data object NotStarted : VerifyPhoneState()

}