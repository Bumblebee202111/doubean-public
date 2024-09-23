package com.github.bumblebee202111.doubean.feature.subjects.books

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.ui.subjectCollection

@Composable
fun BooksScreen(modifier: Modifier = Modifier, viewModel: BooksViewModel = hiltViewModel()) {
    val moviesUiState by viewModel.moviesUiState.collectAsStateWithLifecycle()
    BooksScreen(uiState = moviesUiState, modifier)
}

@Composable
fun BooksScreen(uiState: BooksUiState, modifier: Modifier = Modifier) {
    when (uiState) {
        BooksUiState.Loading -> {
            
        }

        BooksUiState.Error -> {
            
        }

        is BooksUiState.Success -> {
            LazyColumn(modifier = modifier) {
                subjectCollection(uiState.title, uiState.items)
            }
        }
    }
}