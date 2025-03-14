package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.LoginResult
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar

const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"

@Composable
fun LoginScreen(
    onSaveIsLoginSuccessSuccessfulChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {

    //val isFormValid by viewModel.isFormValid.collectAsState()
    val sessionLoginResult by viewModel.sessionLoginResult.collectAsStateWithLifecycle()
    val phoneNumber = viewModel.phoneNumber
    val password = viewModel.password
    val loginResult by viewModel.loginResult.collectAsStateWithLifecycle()
    val message by viewModel.errorMessage.collectAsStateWithLifecycle()

    LoginScreen(
        sessionLoginResult = sessionLoginResult,
        phoneNumber = phoneNumber,
        password = password,
        loginResult = loginResult,
        message = message,
        updatePhoneNumber = viewModel::updatePhoneNumber,
        updatePassword = viewModel::updatePassword,
        triggerAutoImport = viewModel::triggerAutoImport,
        loginWithDoubanSession = viewModel::loginWithDoubanSession,
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
    loginResult: LoginResult?,
    message: String?,
    updatePhoneNumber: (phoneNumberInput: String) -> Unit,
    updatePassword: (passwordInput: String) -> Unit,
    triggerAutoImport: () -> Unit,
    loginWithDoubanSession: (sessionPref: String) -> Unit,
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

            is LoginResult.ShouldVerifyPhone -> {
                onOpenDeepLinkUrl(loginResult.uri)
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

    LaunchedEffect(message) {
        message?.let {
            onShowSnackbar(it)
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
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Text(
                text = "Username/Password Login",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Traditional login method (currently unavailable)",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                enabled = false,
                value = phoneNumber,
                onValueChange = updatePhoneNumber,
                label = { Text("Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            TextField(
                enabled = false,
                value = password,
                onValueChange = updatePassword,
                label = { Text("Enter password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Button(
                onClick = login,
                Modifier.fillMaxWidth(),
                //enabled = isFormValid, currently disabled
                enabled = false
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
                    loginWithDoubanSession(pref)
                },
                Modifier.fillMaxWidth(),
            ) {
                Text(text = "Import Session")
            }
            Text(text = "This minimal UI is intentional — prioritizing functional access\n")

        }
    }

}

