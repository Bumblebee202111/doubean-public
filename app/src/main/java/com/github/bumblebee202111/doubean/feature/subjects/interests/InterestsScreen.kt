package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.model.Movie
import com.github.bumblebee202111.doubean.model.Subject
import com.github.bumblebee202111.doubean.model.SubjectInterest
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.Tv
import com.github.bumblebee202111.doubean.ui.MySubjectItem
import com.github.bumblebee202111.doubean.ui.MySubjectItemMore
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar

@Composable
fun InterestsScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: InterestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.interestsUiState.collectAsStateWithLifecycle()
    InterestsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onBookClick = onBookClick,
        onUpdateInterestStatus = viewModel::onUpdateInterestStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsScreen(
    uiState: InterestsUiState,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onUpdateInterestStatus: (subject: SubjectWithInterest<*>, newStatus: SubjectInterest.Status) -> Unit,
) {

    when (uiState) {
        is InterestsUiState.Success -> {
            Scaffold(topBar = {
                DoubeanTopAppBar(
                    titleText = stringResource(
                        id = R.string.title_my_subject,
                        uiState.title
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }) { innerPadding ->
                LazyColumn(contentPadding = innerPadding) {
                    items(items = uiState.interests, key = { it.first.titleEng }) { interest ->
                        Text(
                            text = "${interest.first.title}（${interest.first.count}）",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(items = interest.second, key = { it.subject.id }) {
                                MySubjectItem(
                                    subject = it,
                                    isLoggedIn = uiState.isLoggedIn,
                                    onClick = {
                                        when (it.subject) {
                                            is Movie -> onMovieClick(it.subject.id)
                                            is Tv -> onTvClick(it.subject.id)
                                            is Book -> onBookClick(it.subject.id)
                                            is Subject.Unsupported -> {
                                            }
                                        }
                                    },
                                    onUpdateStatus = onUpdateInterestStatus
                                )
                            }
                            val moreSubjectCount = interest.first.count - interest.second.size
                            if (moreSubjectCount > 0) {
                                item {
                                    MySubjectItemMore(moreSubjectCount)
                                }
                            }

                        }
                        if (interest != uiState.interests.last()) {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

        }

        InterestsUiState.Error -> {
            //TODO
        }

        InterestsUiState.Loading -> {
            //TODO
        }
    }
}
