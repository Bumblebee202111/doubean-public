package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.VerifyPhoneRequestCodeResult
import com.github.bumblebee202111.doubean.model.VerifyPhoneVerifyCodeResult
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar

@Composable
fun VerifyPhoneScreen(
    onPopBackStack: () -> Unit,
    viewModel: VerifyPhoneViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit,
) {
    val displaySuccess = viewModel.displaySuccess
    val errorMessage = viewModel.errorMessage
    val code = viewModel.code
    val isCodeValid = viewModel.isCodeValid
    val requestCodeResult by viewModel.requestCodeResult.collectAsStateWithLifecycle()
    val verifyPhoneVerifyCodeResult by viewModel.verifyCodeResult.collectAsStateWithLifecycle()
    VerifyPhoneScreen(
        displaySuccess = displaySuccess,
        errorMessage = errorMessage,
        code = code,
        isCodeValid = isCodeValid,
        requestCodeResult = requestCodeResult,
        verifyPhoneVerifyCodeResult = verifyPhoneVerifyCodeResult,
        clearDisplaySuccessState = viewModel::clearDisplaySuccessState,
        clearDisplayErrorState = viewModel::clearDisplayErrorState,
        requestSendCode = viewModel::requestSendCode,
        updateCodeInput = viewModel::updateCodeInput,
        submitCode = viewModel::submitCode,
        onPopBackStack = onPopBackStack,
        onShowSnackbar = onShowSnackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPhoneScreen(
    displaySuccess: Boolean,
    errorMessage: String?,
    code: String,
    isCodeValid: Boolean,
    requestCodeResult: VerifyPhoneRequestCodeResult?,
    verifyPhoneVerifyCodeResult: VerifyPhoneVerifyCodeResult?,
    clearDisplaySuccessState: () -> Unit,
    clearDisplayErrorState: () -> Unit,
    requestSendCode: () -> Unit,
    updateCodeInput: (String) -> Unit,
    submitCode: () -> Unit,
    onPopBackStack: () -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {

    LaunchedEffect(key1 = verifyPhoneVerifyCodeResult) {
        when (verifyPhoneVerifyCodeResult) {
            is VerifyPhoneVerifyCodeResult.Success -> {
                onPopBackStack()
            }

            else -> Unit
        }
    }

    LaunchedEffect(key1 = displaySuccess) {
        if (displaySuccess) {
            onShowSnackbar("SUCCESS")
            clearDisplaySuccessState()
        }
    }

    LaunchedEffect(key1 = errorMessage) {
        if (errorMessage != null) {
            onShowSnackbar(errorMessage)
            clearDisplayErrorState()
        }
    }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(titleText = "Verify Phone")
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            TextButton(onClick = requestSendCode) {
                Text("Send")
            }

            TextField(
                value = code,
                onValueChange = { updateCodeInput(it) },
                enabled = true,//requestCodeResult is VerifyPhoneRequestCodeResult.Success,
                label = { Text("Enter received verification code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            TextButton(
                onClick = submitCode,
                enabled = isCodeValid
            ) {
                Text("Verify")
            }
        }

    }
}