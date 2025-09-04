package com.github.bumblebee202111.doubean.feature.login

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.auth.LoginResult
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar

const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"

@Composable
fun LoginScreen(
    onSaveIsLoginSuccessSuccessfulChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    onOpenDeepLinkUrl: (url: String) -> Boolean,
) {

    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()
    val sessionLoginResult by viewModel.sessionLoginResult.collectAsStateWithLifecycle()
    val phoneNumber = viewModel.phoneNumber
    val password = viewModel.password
    val loginResult by viewModel.loginResult.collectAsStateWithLifecycle()
    val solutionUri by viewModel.solutionUri.collectAsStateWithLifecycle()

    LoginScreen(
        sessionLoginResult = sessionLoginResult,
        phoneNumber = phoneNumber,
        password = password,
        isFormValid = isFormValid,
        loginResult = loginResult,
        solutionUri = solutionUri,
        updatePhoneNumber = viewModel::updatePhoneNumber,
        updatePassword = viewModel::updatePassword,
        triggerAutoImport = viewModel::triggerAutoImport,
        onSubmitSessionPreference = viewModel::loginWithDoubanSession,
        login = viewModel::login,
        clearSolutionUri = viewModel::clearSolutionUri,
        onSaveIsLoginSuccessSuccessfulChange = onSaveIsLoginSuccessSuccessfulChange,
        onPopBackStack = onBackClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl
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
    solutionUri: String?,
    updatePhoneNumber: (phoneNumberInput: String) -> Unit,
    updatePassword: (passwordInput: String) -> Unit,
    triggerAutoImport: () -> Unit,
    onSubmitSessionPreference: (sessionPref: String) -> Unit,
    login: () -> Unit,
    clearSolutionUri: () -> Unit,
    onSaveIsLoginSuccessSuccessfulChange: (Boolean) -> Unit,
    onPopBackStack: () -> Unit,
    onOpenDeepLinkUrl: (url: String) -> Boolean,
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

    if (solutionUri != null) {
        LaunchedEffect(solutionUri) {
            Log.d("SolutionNavigationEffect", "Processing solution URI: $solutionUri")
            onOpenDeepLinkUrl(solutionUri)
            clearSolutionUri()
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
        text = stringResource(R.string.login_phone_password_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = updatePhoneNumber,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.login_phone_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )

    OutlinedTextField(
        value = password,
        onValueChange = updatePassword,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.login_password_label)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Button(
        onClick = login,
        modifier = Modifier.fillMaxWidth(),
        enabled = isFormValid
    ) {
        Text(text = stringResource(R.string.login_button))
    }
}

@SuppressLint("SdCardPath")
@Composable
private fun SessionImportSection(
    triggerAutoImport: () -> Unit,
    onSubmitSessionPreference: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Text(
        text = stringResource(R.string.login_session_import_title),
        style = MaterialTheme.typography.titleMedium
    )
    Text(
        text = stringResource(R.string.login_session_import_note),
        style = MaterialTheme.typography.bodyMedium
    )
    TextButton(onClick = triggerAutoImport, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.login_session_import_auto_button))
    }
    Text(
        text = stringResource(R.string.login_session_import_manual_note),
        style = MaterialTheme.typography.bodyMedium
    )
    var pref by remember { mutableStateOf("") }
    OutlinedTextField(
        value = pref,
        onValueChange = { pref = it },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(stringResource(R.string.login_session_import_textfield_label)) },
        supportingText = {
            Column {
                val path =
                    "/data/data/com.douban.frodo/shared_prefs/com.douban.frodo_preferences.xml"
                val format = "<string name=\"key_current_account_info\">...</string>"
                Text(stringResource(R.string.label_path, path))
                Text(stringResource(R.string.label_format, format))
            }
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
        Text(text = stringResource(R.string.login_session_import_manual_button))
    }
}

@Composable
private fun FinalReminderText() {
    Text(
        text = stringResource(R.string.login_final_reminder),
        style = MaterialTheme.typography.bodySmall
    )
}
