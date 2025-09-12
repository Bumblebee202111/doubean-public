package com.github.bumblebee202111.doubean.feature.subjects.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.search.common.searchHistory
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectItemBasicContent
import com.github.bumblebee202111.doubean.model.search.SearchHistory
import com.github.bumblebee202111.doubean.model.subjects.SearchResultSubjectItem
import com.github.bumblebee202111.doubean.model.subjects.SubjectSubTag
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.model.subjects.SubjectsSearchType
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItem
import com.github.bumblebee202111.doubean.ui.component.DoubeanAppBarWithSearch
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun SearchSubjectsScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: SearchSubjectsViewModel = hiltViewModel(),
) {
    val searchResultUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val history by viewModel.searchHistory.collectAsStateWithLifecycle()

    SearchSubjectsScreen(
        uiState = searchResultUiState,
        history = history,
        onSearchTriggered = viewModel::onSearchTriggered,
        onBackClick = onBackClick,
        onQueryChanged = viewModel::onQueryChanged,
        onDeleteHistoryItem = viewModel::onDeleteHistoryItem,
        onClearHistory = viewModel::onClearHistory,
        onTypeSelected = viewModel::onTypeSelected,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onBookClick = onBookClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSubjectsScreen(
    uiState: SearchResultUiState,
    history: List<SearchHistory>,
    onBackClick: () -> Unit,
    onQueryChanged: (query: String) -> Unit,
    onSearchTriggered: (query: String) -> Unit,
    onDeleteHistoryItem: (String) -> Unit,
    onClearHistory: () -> Unit,
    onTypeSelected: (SubjectsSearchType) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    var isSearchFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchAndHideKeyboard = { query: String ->
        keyboardController?.hide()
        onSearchTriggered(query)
    }

    Scaffold(
        topBar = {
            DoubeanAppBarWithSearch(
                query = uiState.query,
                onQueryChange = onQueryChanged,
                onSearch = onSearchAndHideKeyboard,
                onBackClick = onBackClick,
                onFocusChanged = { isSearchFocused = it },
                placeholderText = stringResource(R.string.search_subjects_hint)
            )
        }
    ) { innerPadding ->

        if (uiState.query.isBlank() && isSearchFocused) {
            LazyColumn(contentPadding = innerPadding) {
                searchHistory(
                    history = history,
                    onHistoryClick = onSearchAndHideKeyboard,
                    onDeleteClick = onDeleteHistoryItem,
                    onClearAllClick = onClearHistory
                )
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                val items = uiState.items
                uiState.types?.let { types ->
                    TypeFilter(
                        types = types,
                        selectedType = uiState.selectedType,
                        onTypeSelected = onTypeSelected
                    )
                }

                when {
                    uiState.isLoading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                    items == null -> Unit
                    items.isEmpty() -> Text(
                        stringResource(R.string.empty_search_result),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                            .wrapContentSize()
                    )

                    else -> SearchResultBody(
                        type = uiState.selectedType,
                        searchResultSubjects = items,
                        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                        onMovieClick = onMovieClick,
                        onTvClick = onTvClick,
                        onBookClick = onBookClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeFilter(
    types: List<SubjectSubTag>,
    selectedType: SubjectsSearchType?,
    onTypeSelected: (SubjectsSearchType) -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        types.forEach { type ->
            val selected = type.type == selectedType
            FilterChip(
                selected = selected,
                label = { Text(type.name) },
                onClick = { onTypeSelected(type.type) }
            )
        }
    }
}

@Composable
private fun SearchResultBody(
    type: SubjectsSearchType?,
    searchResultSubjects: List<SearchResultSubjectItem>,
    contentPadding: PaddingValues,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(items = searchResultSubjects, key = { it.id }) { subject ->
            val context = LocalContext.current
            SubjectItem(
                basicContent = {
                    SubjectItemBasicContent(
                        subject = subject,
                        showType = type == SubjectsSearchType.MOVIES_AND_TVS
                    )
                },
                onClick = {
                    when (subject.type) {
                        SubjectType.MOVIE -> {
                            onMovieClick(subject.id)
                        }

                        SubjectType.TV -> {
                            onTvClick(subject.id)
                        }

                        SubjectType.BOOK -> {
                            onBookClick(subject.id)
                        }

                        SubjectType.UNSUPPORTED -> {
                            OpenInUtils.openInDouban(context, subject.uri)
                        }
                    }
                },

                )
        }
    }
}

