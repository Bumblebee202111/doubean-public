package com.github.bumblebee202111.doubean.feature.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.compose.content
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.github.bumblebee202111.doubean.model.LoginResult
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }

    private lateinit var savedStateHandle: SavedStateHandle

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        LoginScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle[LOGIN_SUCCESSFUL] = false

        repeatWithViewLifecycle {
            launch {
                viewModel.loginResult.collect { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            savedStateHandle[LOGIN_SUCCESSFUL] = true
                            findNavController().popBackStack()
                        }

                        is LoginResult.Error -> {
                            //showErrorMessage
                        }

                        is LoginResult.ShouldVerifyPhone -> {
                            val request =
                                NavDeepLinkRequest.Builder.fromUri(result.uri.toUri())
                                    .build()
                            findNavController().navigate(request)
                        }

                        null -> {

                        }
                    }

                }

            }
            launch {
                viewModel.sessionLoginResult.collect {
                    when (it) {
                        true -> findNavController().popBackStack()
                        false -> {
                            //show errorMessage
                        }

                        null -> {

                        }
                    }
                }
            }

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen(
        viewModel: LoginViewModel = this@LoginFragment.viewModel,
    ) {
        //val isFormValid by viewModel.isFormValid.collectAsState()

        val message by viewModel.errorMessage.collectAsStateWithLifecycle()

        val snackbarHostState = remember {
            SnackbarHostState()
        }

        LaunchedEffect(message) {
            message?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessage()
            }
        }


        Scaffold(
            topBar = {
                DoubeanTopAppBar(titleText = "Login")
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
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
                        viewModel.loginWithDoubanSession(pref)
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
                    value = viewModel.phoneNumber,
                    onValueChange = viewModel::updatePhoneNumber,
                    label = { Text("Phone number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                TextField(
                    enabled = false,
                    value = viewModel.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Button(
                    onClick = viewModel::login,
                    Modifier.fillMaxWidth(),
                    //enabled = isFormValid, currently disabled
                    enabled = false
                ) {
                    Text(text = "Login")
                }
            }
        }

    }

}

