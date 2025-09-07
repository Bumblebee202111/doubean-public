package com.github.bumblebee202111.doubean.feature.subjects.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectItemBasicContent
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
    SearchSubjectsScreen(
        uiState = searchResultUiState,
        onSearchTriggered = viewModel::onSearchTriggered,
        onBackClick = onBackClick,
        onQueryChanged = viewModel::onQueryChanged,
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
    onBackClick: () -> Unit,
    onQueryChanged: (query: String) -> Unit,
    onSearchTriggered: () -> Unit,
    onTypeSelected: (SubjectsSearchType) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    Scaffold(
        topBar = {
            DoubeanAppBarWithSearch(
                query = uiState.query,
                onQueryChange = onQueryChanged,
                onSearch = { onSearchTriggered() },
                onBackClick = onBackClick,
                placeholderText = stringResource(R.string.search_subjects_hint)
            )
        }
    ) { innerPadding ->
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
            if (subject != searchResultSubjects.last()) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

