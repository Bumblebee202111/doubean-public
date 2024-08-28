package com.github.bumblebee202111.doubean.feature.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.compose.AndroidFragment
import com.github.bumblebee202111.doubean.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDefaultNotificationsPreferencesSettingsScreen(
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.per_follow_default_notifications_preferences_settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                })
        }
    ) {
        AndroidFragment<GroupDefaultNotificationsPreferencesSettingsFragment>(
            modifier = Modifier.padding(
                it
            )
        )
    }
}