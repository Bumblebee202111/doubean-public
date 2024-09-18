package com.github.bumblebee202111.doubean.feature.subjects.movies

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.ui.SubjectItem

@Composable
fun MoviesScreen(modifier: Modifier = Modifier, viewModel: MoviesViewModel = hiltViewModel()) {
    val moviesUiState by viewModel.moviesUiState.collectAsStateWithLifecycle()
    MoviesScreen(uiState = moviesUiState, modifier)
}

@Composable
fun MoviesScreen(uiState: MoviesUiState, modifier: Modifier = Modifier) {
    when (uiState) {
        MoviesUiState.Loading -> {
        }

        MoviesUiState.Error -> {

        }

        is MoviesUiState.Success -> {
            LazyColumn(modifier = modifier) {
                item {
                    Text(
                        text = uiState.title,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.displayMedium
                    )
                }

                val items = uiState.items
                items(items = items, key = { it.id }) { movie ->
                    SubjectItem(subject = movie, position = items.indexOf(movie) + 1)
                    if (movie != items.last()) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}