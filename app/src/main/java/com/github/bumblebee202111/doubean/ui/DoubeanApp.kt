package com.github.bumblebee202111.doubean.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun DoubeanApp(startWithGroups: Boolean) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        MainNavScreen(
            startWithGroups = startWithGroups,
            onShowSnackbar = snackbarHostState::showSnackbar,
            modifier = Modifier.padding(it)
        )
    }

}