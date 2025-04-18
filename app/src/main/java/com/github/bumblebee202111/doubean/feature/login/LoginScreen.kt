package com.github.bumblebee202111.doubean.feature.login

import android.util.Log
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
import com.github.bumblebee202111.doubean.model.auth.LoginResult
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
        onSubmitSessionPreference = viewModel::loginWithDoubanSession,
        login = viewModel::login,
        clearError = viewModel::clearMessage,
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
    onSubmitSessionPreference: (sessionPref: String) -> Unit,
    login: () -> Unit,
    clearError: () -> Unit,
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
            (it as? ApiError)?.solutionUri?.let { solutionUri ->
                try {
                    onOpenDeepLinkUrl(solutionUri)
                } catch (e: Exception) {
                    Log.e("onOpenDeepLinkUrl", "Failed to open solutionUri", e)
                }
            } ?: onShowSnackbar(message)
            clearError()
        }
    }

    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleText = "Login",
                navigationIcon = {
                    IconButton(onClick = onPopBackStack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LoginSection(
                phoneNumber = phoneNumber,
                password = password,
                isFormValid = isFormValid,
                updatePhoneNumber = updatePhoneNumber,
                updatePassword = updatePassword,
                login = login
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))
            SessionImportSection(
                triggerAutoImport = triggerAutoImport,
                onSubmitSessionPreference = onSubmitSessionPreference,
            )
            Spacer(modifier = Modifier.height(24.dp))
            FinalReminderText()
        }
    }
}

@Composable
private fun LoginSection(
    phoneNumber: String,
    password: String,
    isFormValid: Boolean,
    updatePhoneNumber: (String) -> Unit,
    updatePassword: (String) -> Unit,
    login: () -> Unit,
) {
    Text(
        text = "Phone/Password Login (BETA)",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.error
    )
    Text(
        text = "Need feedback on whether it works.",
        style = MaterialTheme.typography.bodySmall
    )

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = updatePhoneNumber,
        modifier = Modifier.fillMaxWidth(),
        enabled = SHOULD_ENABLE_PHONE_PASSWORD_LOGIN,
        label = { Text("Phone") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )

    OutlinedTextField(
        value = password,
        onValueChange = updatePassword,
        modifier = Modifier.fillMaxWidth(),
        enabled = SHOULD_ENABLE_PHONE_PASSWORD_LOGIN,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Button(
        onClick = login,
        modifier = Modifier.fillMaxWidth(),
        enabled = isFormValid && SHOULD_ENABLE_PHONE_PASSWORD_LOGIN
    ) {
        Text(text = "Login")
    }
}

@Composable
private fun SessionImportSection(
    triggerAutoImport: () -> Unit,
    onSubmitSessionPreference: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Text(
        text = "Douban app Session Import (Advanced)",
        style = MaterialTheme.typography.titleMedium
    )
    Text(
        text = "Note: This method typically requires root access. " +
                "Automatic sync should happen at app startup if it is enabled in Settings and root access is granted.",
        style = MaterialTheme.typography.bodyMedium
    )
    TextButton(onClick = triggerAutoImport, modifier = Modifier.fillMaxWidth()) {
        Text("Auto-Import Session Now")
    }
    Text(
        text = "For non-rooted phones:\n" +
                "Copy from a rooted device and paste here\n" +
                "⚠️ Manual entry not recommended because:\n" +
                "• Complicated process\n" +
                "• Session expiration\n",
        style = MaterialTheme.typography.bodyMedium
    )
    var pref by remember { mutableStateOf("") }
    OutlinedTextField(
        value = pref,
        onValueChange = { pref = it },
        modifier = Modifier
            .fillMaxWidth(),
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
            onSubmitSessionPreference(pref)
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Import Session")
    }
}

@Composable
private fun FinalReminderText() {
    Text(
        text = "While we mimic official login behavior without third-party services, use login-related features at your own risk. This minimal UI is intentional - prioritizing functional access\n"
    )
}

private const val SHOULD_ENABLE_PHONE_PASSWORD_LOGIN = true
