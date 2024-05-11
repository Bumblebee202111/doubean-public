package com.github.bumblebee202111.doubean.feature.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.VerifyPhoneRequestCodeResult
import com.github.bumblebee202111.doubean.model.VerifyPhoneVerifyCodeResult
import com.github.bumblebee202111.doubean.ui.common.repeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyPhoneFragment : Fragment() {
    val viewModel: VerifyPhoneViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        VerifyPhoneScreen()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repeatWithViewLifecycle {
            launch {
                viewModel.verifyCodeResult.collect {
                    when (it) {
                        is VerifyPhoneVerifyCodeResult.Success -> {
                            Toast.makeText(requireContext(), "SUCCESS", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack(R.id.nav_login, false)
                        }

                        is VerifyPhoneVerifyCodeResult.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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
    fun VerifyPhoneScreen(viewModel: VerifyPhoneViewModel = this@VerifyPhoneFragment.viewModel) {
        Column {
            TopAppBar(
                {
                    Text("123")
                })
            TextButton(onClick = viewModel::requestSendCode) {
                Text("Send")
            }

            val requestCodeResult by viewModel.requestCodeResult.collectAsState()
            TextField(
                value = viewModel.code,
                onValueChange = { viewModel.updateCodeInput(it) },
                enabled = requestCodeResult is VerifyPhoneRequestCodeResult.Success,
                label = { Text("Enter received verification code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            TextButton(
                onClick = viewModel::submitCode,
                enabled = viewModel.isCodeValid
            ) {
                Text("Verify")
            }
        }
    }

}