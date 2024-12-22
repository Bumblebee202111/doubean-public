package com.github.bumblebee202111.doubean.feature.subjects.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.SearchResultSubjectItem
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.model.SubjectsSearchType
import com.github.bumblebee202111.doubean.ui.SubjectItem
import com.github.bumblebee202111.doubean.ui.SubjectItemBasicContent
import com.github.bumblebee202111.doubean.ui.component.SearchTextField
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun SearchSubjectsScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: SearchSubjectsViewModel = hiltViewModel(),
) {
    val type = viewModel.type
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle()
    SearchSubjectsScreen(
        type = type,
        searchResultUiState = searchResultUiState,
        onSearchTriggered = viewModel::onSearchTriggered,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onBookClick = onBookClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSubjectsScreen(
    type: SubjectsSearchType,
    searchResultUiState: SearchResultUiState,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                SearchTextField(
                    labelTextResId = getSearchLabelTextResId(type),
                    modifier = Modifier.fillMaxWidth(),
                    onQueryChange = {},
                    onSearchTriggered = onSearchTriggered
                )
            },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })


        }
    ) {
        when (searchResultUiState) {
            SearchResultUiState.EmptyQuery -> {
                /* TODO */
            }

            SearchResultUiState.LoadFailed -> {
                /* TODO */
            }

            SearchResultUiState.Loading -> {
                /* TODO */
            }

            is SearchResultUiState.Success -> {
                SearchResultBody(
                    type = type,
                    searchResultSubjects = searchResultUiState.subjects,
                    contentPadding = it,
                    onMovieClick = onMovieClick,
                    onTvClick = onTvClick,
                    onBookClick = onBookClick
                )
            }
        }
    }
}

@Composable
private fun SearchResultBody(
    type: SubjectsSearchType,
    searchResultSubjects: List<SearchResultSubjectItem>,
    contentPadding: PaddingValues,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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

private fun getSearchLabelTextResId(searchType: SubjectsSearchType): Int {
    return when (searchType) {
        SubjectsSearchType.MOVIES_AND_TVS -> R.string.search_movies_and_tvs_hint
        SubjectsSearchType.BOOKS -> R.string.search_books_hint
    }
}