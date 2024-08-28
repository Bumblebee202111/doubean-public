package com.github.bumblebee202111.doubean.feature.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.compose.AndroidFragment
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            DoubeanTopAppBar(
                titleResId = R.string.settings,
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
        AndroidFragment<SettingsContentFragment>(modifier = Modifier.padding(it))
    }
}