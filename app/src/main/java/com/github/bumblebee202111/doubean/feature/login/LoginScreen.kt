package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.ApiError
import com.github.bumblebee202111.doubean.model.AppError
import com.github.bumblebee202111.doubean.model.LoginResult
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.uiMessage

const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"

@Composable
fun LoginScreen(
    onSaveIsLoginSuccessSuccessfulChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {

    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()
    val sessionLoginResult by viewModel.sessionLoginResult.collectAsStateWithLifecycle()
    val phoneNumber = viewModel.phoneNumber
    val password = viewModel.password
    val loginResult by viewModel.loginResult.collectAsStateWithLifecycle()
    val uiError by viewModel.uiError.collectAsStateWithLifecycle()

    LoginScreen(
        sessionLoginResult = sessionLoginResult,
        phoneNumber = phoneNumber,
        password = password,
        isFormValid = isFormValid,
        loginResult = loginResult,
        uiError = uiError,
        updatePhoneNumber = viewModel::updatePhoneNumber,
        updatePassword = viewModel::updatePassword,
        triggerAutoImport = viewModel::triggerAutoImport,
        importDoubanSession = viewModel::loginWithDoubanSession,
        login = viewModel::login,
        clearMessage = viewModel::clearMessage,
        onSaveIsLoginSuccessSuccessfulChange = onSaveIsLoginSuccessSuccessfulChange,
        onPopBackStack = onBackClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onShowSnackbar = onShowSnackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    sessionLoginResult: Boolean?,
    phoneNumber: String,
    password: String,
    isFormValid: Boolean,
    loginResult: LoginResult?,
    uiError: AppError?,
    updatePhoneNumber: (phoneNumberInput: String) -> Unit,
    updatePassword: (passwordInput: String) -> Unit,
    triggerAutoImport: () -> Unit,
    importDoubanSession: (sessionPref: String) -> Unit,
    login: () -> Unit,
    clearMessage: () -> Unit,
    onSaveIsLoginSuccessSuccessfulChange: (Boolean) -> Unit,
    onPopBackStack: () -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {

    LaunchedEffect(loginResult) {
        when (loginResult) {
            is LoginResult.Success -> {
                onSaveIsLoginSuccessSuccessfulChange(true)
                onPopBackStack()
            }

            is LoginResult.Error -> {
                val appError = loginResult.appError
                (appError as? ApiError)?.solutionUri?.let { solutionUri ->
                    try {
                        onOpenDeepLinkUrl(solutionUri)
                    } catch (_: Exception) {
                    }
                }
            }

            else -> Unit

        }
    }

    LaunchedEffect(key1 = sessionLoginResult) {
        when (sessionLoginResult) {
            true -> onPopBackStack()
            else -> Unit
        }
    }


    uiError?.let {
        val message = uiError.uiMessage
        LaunchedEffect(uiError) {
            onShowSnackbar(message)
            clearMessage()
        }
    }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(titleText = "Login",
                navigationIcon = {
                    IconButton(onClick = onPopBackStack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)

        ) {

            Text(
                text = "Phone/Password Login (Experimental)",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "WARNING: Use at your own risk!",
                color = MaterialTheme.colorScheme.error
            )
            Text(
                "Actual login attempts may trigger Douban's risk control systems.\n" +
                        "Always prefer session import if root access is available."
            )
            OutlinedTextField(
                enabled = SHOULD_ENABLE_PHONE_PASSWORD_LOGIN,
                value = phoneNumber,
                onValueChange = updatePhoneNumber,
                label = { Text("Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            TextField(
                enabled = SHOULD_ENABLE_PHONE_PASSWORD_LOGIN,
                value = password,
                onValueChange = updatePassword,
                label = { Text("Enter password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Button(
                onClick = login,
                Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(text = "Login")
            }

            HorizontalDivider()

            Text(
                text = "Douban App Session Import",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Note: This method typically requires root access. " +
                        "Automatic sync should happen at app startup if it is enabled in Settings & root is granted.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = triggerAutoImport) {
                Text("Auto-Import Session Now")
            }
            Text(
                text = "For non-rooted phones:\n" +
                        "Copy from rooted device → Paste here\n\n" +
                        "⚠️Manual submission not recommended because:\n" +
                        "• Unmaintained\n" +
                        "• Multi-step process\n" +
                        "• Frequent expirations\n" +
                        "• Unstable",

                style = MaterialTheme.typography.bodyMedium
            )


            var pref by remember { mutableStateOf("") }

            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                value = pref,
                onValueChange = { pref = it },
                Modifier.fillMaxWidth(),
                label = { Text("key_current_account_info preference line") },
                supportingText = {
                    Text(
                        "Path: /data/data/com.douban.frodo/shared_prefs/com.douban.frodo_preferences.xml\n" +
                                "Format:    <string name=\"key_current_account_info\">...</string>"
                    )
                },
                maxLines = 5,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    importDoubanSession(pref)
                },
                Modifier.fillMaxWidth(),
            ) {
                Text(text = "Import Session")
            }
            Text(
                text = "This minimal UI is intentional - prioritizing functional access\n" +
                        "Use discreetly/低调使用"
            )
        }
    }

}

private const val SHOULD_ENABLE_PHONE_PASSWORD_LOGIN = true
