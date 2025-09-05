package com.github.bumblebee202111.doubean.feature.doulists.createddoulists

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.doulists.userdoulists.UserDouListsContent
import com.github.bumblebee202111.doubean.feature.doulists.userdoulists.UserDouListsUiState
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.doubeanTopAppBarHeight

// CreatedDouListActivity/UserDouListsFragment
// Can be reused if following Doulists is to be supported
@Composable
fun CreatedDouListsScreen(
    onBackClick: () -> Unit,
    onDouListClick: (douListId: String) -> Unit,
    viewModel: CreatedDouListsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CreatedDouListsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onDouListClick = onDouListClick,
        onRetryClick = { viewModel.onRetry() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatedDouListsScreen(
    uiState: UserDouListsUiState,
    onBackClick: () -> Unit,
    onDouListClick: (douListId: String) -> Unit,
    onRetryClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (uiState is UserDouListsUiState.Success) {
                            val currentUser = uiState.douLists.user
                            val fallbackPainter =
                                rememberVectorPainter(image = Icons.Filled.AccountCircle)
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(currentUser.avatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = currentUser.name,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                fallback = fallbackPainter,
                                error = fallbackPainter
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = stringResource(id = R.string.title_created_doulist),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                actions = {
                    if (uiState is UserDouListsUiState.Error) {
                        IconButton(onClick = onRetryClick) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                        }
                    }
                },
                expandedHeight = doubeanTopAppBarHeight
            )
        }
    ) { innerPadding ->
        UserDouListsContent(
            uiState = uiState,
            onItemClick = onDouListClick,
            onRetryClick = onRetryClick,
            contentPadding = innerPadding
        )
    }
}

