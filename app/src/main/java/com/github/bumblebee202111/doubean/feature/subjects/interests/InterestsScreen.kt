package com.github.bumblebee202111.doubean.feature.subjects.interests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.model.Movie
import com.github.bumblebee202111.doubean.model.Subject
import com.github.bumblebee202111.doubean.model.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.SubjectWithInterest
import com.github.bumblebee202111.doubean.model.Tv
import com.github.bumblebee202111.doubean.ui.MySubjectItem
import com.github.bumblebee202111.doubean.ui.MySubjectItemMore
import com.github.bumblebee202111.doubean.ui.SubjectItemImage
import com.github.bumblebee202111.doubean.ui.SubjectStatusActionTextResIdsMap
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import kotlinx.coroutines.launch

@Composable
fun InterestsScreen(
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: InterestsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.interestsUiState.collectAsStateWithLifecycle()
    val moreInterestPagingItems = viewModel.moreInterestsPagingData.collectAsLazyPagingItems()
    InterestsScreen(
        uiState = uiState,
        moreInterestPagingItems = moreInterestPagingItems,
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
    moreInterestPagingItems: LazyPagingItems<SubjectWithInterest<Subject>>,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onUpdateInterestStatus: (subject: SubjectWithInterest<*>, newStatus: SubjectInterestStatus) -> Unit,
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
                val lazyListState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()
                val onSubjectClick: (SubjectWithInterest<*>) -> Unit = {
                    when (it.subject) {
                        is Movie -> onMovieClick(it.subject.id)
                        is Tv -> onTvClick(it.subject.id)
                        is Book -> onBookClick(it.subject.id)
                        is Subject.Unsupported -> {
                        }
                    }
                }
                val onMoreClick: () -> Unit = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(uiState.statusesAndInterests.size)
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(bottom = 8.dp),
                    state = lazyListState,
                    contentPadding = innerPadding
                ) {
                    items(
                        items = uiState.statusesAndInterests,
                        key = { it.first.titleEng }) { interest ->
                        InterestsSectionTitleText(
                            text = "${interest.first.title}（${interest.first.count}）"
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
                                    onClick = { onSubjectClick(it) },
                                    onUpdateStatus = onUpdateInterestStatus
                                )
                            }
                            val moreSubjectCount = interest.first.count - interest.second.size
                            if (moreSubjectCount > 0) {
                                item {
                                    MySubjectItemMore(
                                        moreCount = moreSubjectCount,
                                        onClick = onMoreClick
                                    )
                                }
                            }

                        }
                        if (interest != uiState.statusesAndInterests.last()) {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                    }

                    if (uiState.hasMore) {
                        
                        item {
                            InterestsSectionTitleText(stringResource(R.string.more))
                        }
                        items(
                            count = moreInterestPagingItems.itemCount,
                            key = moreInterestPagingItems.itemKey { it.id }) { index ->
                            moreInterestPagingItems[index]?.let {
                                SubjectArchiveItem(subject = it, onClick = { onSubjectClick(it) })
                            }
                        }
                    }


                }
            }

        }

        InterestsUiState.Error -> {
            
        }

        InterestsUiState.Loading -> {
            
        }
    }
}


@Composable
private fun InterestsSectionTitleText(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun SubjectArchiveItem(subject: SubjectWithInterest<*>, onClick: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        
        SubjectItemImage(url = subject.subject.imageUrl)
        Spacer(Modifier.width(16.dp))
        Column {
            
            Text(
                text = stringResource(
                    SubjectStatusActionTextResIdsMap.getValue(subject.type)
                        .getValue(subject.interest.status)
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            
            Text(
                text = subject.subject.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}