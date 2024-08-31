package com.github.bumblebee202111.doubean.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
    onPopBackStack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    onOpenDeepLinkUrl: (url: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {

    
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
        loginWithDoubanSession = viewModel::loginWithDoubanSession,
        login = viewModel::login,
        clearMessage = viewModel::clearMessage,
        onSaveIsLoginSuccessSuccessfulChange = onSaveIsLoginSuccessSuccessfulChange,
        onPopBackStack = onPopBackStack,
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
            DoubeanTopAppBar(titleText = "Login")
        },
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "Manually sync login session from douban app preferences",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Note accessing the required preference usually requires root permission.\n" +
                        "Manual syncing is highly discouraged since\n" +
                        "1. The feature is not carefully designed and you may meet weird bugs\n" +
                        "2. It requires many steps and extra care\n" +
                        "Sync usually can be done automatically at app startup if root is granted",
                style = MaterialTheme.typography.bodyMedium
            )
            var pref by remember { mutableStateOf("") }

            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                value = pref,
                onValueChange = { pref = it },
                Modifier.fillMaxWidth(),
                label = { Text("key_current_account_info pref line") },
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
                Text(text = "Login")
            }
            HorizontalDivider()
            Text(
                text = "Login with username and password",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Not implemented now and not very likely to be implemented in the future due to its difficulty",
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
                
                enabled = false
            ) {
                Text(text = "Login")
            }
        }
    }

}

