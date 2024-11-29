package com.github.bumblebee202111.doubean.feature.subjects.tv

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.Tv
import com.github.bumblebee202111.doubean.ui.SubjectDetailHeader
import com.github.bumblebee202111.doubean.ui.SubjectTopBar

@Composable
fun TvScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: TvViewModel = hiltViewModel(),
) {
    val tvUiState by viewModel.tvUiState.collectAsStateWithLifecycle()
    TvScreen(
        tvUiState = tvUiState,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        onUpdateStatus = viewModel::onUpdateStatus
    )
}

@Composable
fun TvScreen(
    tvUiState: TvUiState,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onUpdateStatus: (subject: SubjectWithInterest<Tv>, newStatus: SubjectInterest.Status) -> Unit,
) {
    Scaffold(
        topBar = {
            TvTopBar(tvUiState = tvUiState, onBackClick = onBackClick)
        }
    ) { innerPadding ->
        when (tvUiState) {
            is TvUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    item {
                        SubjectDetailHeader(
                            subject = tvUiState.tv,
                            isLoggedIn = tvUiState.isLoggedIn,
                            onLoginClick = onLoginClick,
                            onUpdateStatus = onUpdateStatus
                        )
                    }
                }
            }

            else -> {

            }
        }
    }
}

@Composable
private fun TvTopBar(tvUiState: TvUiState, onBackClick: () -> Unit) {
    SubjectTopBar(
        subjectType = SubjectType.TV,
        subject = (tvUiState as? TvUiState.Success)?.tv,
        onBackClick = onBackClick
    )
}
