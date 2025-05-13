package com.github.bumblebee202111.doubean.feature.userprofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage

@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UserProfileScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick,
        onLoginClick = onLoginClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    uiState: UserProfileUiState,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                navigationIcon = {
                    if (!uiState.isTargetingCurrentUser) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                },
                title = { },
                actions = {
                    if (uiState.isTargetingCurrentUser) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }

                    uiState.errorMessage != null -> {
                        
                    }

                    uiState.user != null -> {
                        val user = uiState.user
                        Row(modifier = Modifier.fillMaxWidth()) {
                            UserProfileImage(
                                url = user.avatar,
                                size = dimensionResource(R.dimen.icon_size_extra_large)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(user.name, style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    stringResource(R.string.title_my_user_id, user.uid),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }


                    uiState.isTargetingCurrentUser && !uiState.isLoggedIn -> {
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(text = stringResource(R.string.title_login))
                        }
                    }

                }
            }
        }
    )

}