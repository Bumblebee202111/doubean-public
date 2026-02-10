@file:Suppress("DEPRECATION")

package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.auth.CaptchaSolution
import com.github.bumblebee202111.doubean.model.auth.JCaptcha
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.RexxarWebView
import com.google.accompanist.web.LoadingState

@Composable
fun VerifyPhoneScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: VerifyPhoneViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    VerifyPhoneScreen(
        uiState = uiState,
        onRequestPhoneCode = viewModel::requestPhoneCode,
        onPhoneCodeChanged = viewModel::updatePhoneCode,
        onVerifyCaptcha = viewModel::verifyCaptcha,
        onVerifyPhoneCode = viewModel::verifyPhoneCode,
        onBackClick = onBackClick,
        onSuccess = onSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPhoneScreen(
    uiState: VerifyPhoneMainUiState,
    onRequestPhoneCode: () -> Unit,
    onPhoneCodeChanged: (String) -> Unit,
    onVerifyCaptcha: (solution: CaptchaSolution) -> Unit,
    onVerifyPhoneCode: () -> Unit,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
) {

    if (uiState is VerifyPhoneMainUiState.VerificationSuccess) {
        LaunchedEffect(uiState.message) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                titleText = stringResource(R.string.verify_phone_title)
            )
        },
    ) { innerPadding ->
        when (uiState) {
            is VerifyPhoneMainUiState.Active -> ActiveContent(
                activeUiState = uiState,
                onRequestPhoneCode = onRequestPhoneCode,
                onPhoneCodeChanged = onPhoneCodeChanged,
                onVerifyCaptcha = onVerifyCaptcha,
                onVerifyPhoneCode = onVerifyPhoneCode,
                modifier = Modifier.padding(innerPadding)
            )

            is VerifyPhoneMainUiState.VerificationSuccess -> Unit
        }
    }
}

@Composable
private fun ActiveContent(
    activeUiState: VerifyPhoneMainUiState.Active,
    onRequestPhoneCode: () -> Unit,
    onPhoneCodeChanged: (String) -> Unit,
    onVerifyCaptcha: (solution: CaptchaSolution) -> Unit,
    onVerifyPhoneCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(16.dp)) {

        activeUiState.jCaptcha?.let { captcha ->
            CaptchaWebView(
                jCaptcha = captcha,
                onVerifyCaptcha = onVerifyCaptcha
            )
        }

        OutlinedTextField(
            value = activeUiState.phoneCode,
            onValueChange = { onPhoneCodeChanged(it) },
            enabled = activeUiState.isCodeSent,
            label = { Text(stringResource(R.string.verify_phone_code_label)) },
            isError = !activeUiState.isPhoneCodeValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = onRequestPhoneCode,
            enabled = !activeUiState.isRequestingCode &&
                    activeUiState.jCaptcha == null &&
                    activeUiState.countdownSeconds == 0
        ) {
            if (activeUiState.isRequestingCode) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                val text = if (activeUiState.countdownSeconds > 0) {
                    "${activeUiState.countdownSeconds}s"
                } else {
                    stringResource(R.string.verify_phone_request_code_button)
                }
                Text(text)
            }
        }

        if (activeUiState.isCodeSent) {
            Button(
                onClick = onVerifyPhoneCode,
                enabled = activeUiState.canVerify
            ) {
                if (activeUiState.isVerifyingCode) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.verify_phone_verify_button))
                }
            }
        }
    }
}

@Composable
private fun CaptchaWebView(
    jCaptcha: JCaptcha,
    onVerifyCaptcha: (solution: CaptchaSolution) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        RexxarWebView(
            filename = "",
            initialParams = mapOf(
                "" to jCaptcha.touchCapUrl,
                "" to jCaptcha.tcAppId,
                "" to "true"
            ),
            handleApiRequest = { url, method, headers, params ->
                when {
                    url.contains("/captcha/verify_captcha") -> {
                        val  = params[""] ?: return@RexxarWebView null
                        val  = params[""] ?: return@RexxarWebView null
                        val tcAppId = params[""] ?: return@RexxarWebView null
                        val solution = CaptchaSolution(
                             = ,
                             = ,
                            tcAppId = tcAppId
                        )
                        onVerifyCaptcha(solution)
                        return@RexxarWebView null
                    }

                    else -> return@RexxarWebView null
                }
            },
            onLoadingStateChanged = { state ->
                isLoading = state !is LoadingState.Finished
            })
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.loading))
            }
        }
    }
}