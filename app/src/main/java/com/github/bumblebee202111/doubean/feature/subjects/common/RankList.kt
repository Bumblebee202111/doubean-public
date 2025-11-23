package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.model.subjects.Book
import com.github.bumblebee202111.doubean.model.subjects.Movie
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectCollection
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.model.subjects.Tv
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItem
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemBasicContent
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectItemRank
import com.github.bumblebee202111.doubean.ui.common.subject.SubjectSimpleInterestButton
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry
import com.github.bumblebee202111.doubean.ui.util.toUiMessage
import com.github.bumblebee202111.doubean.util.OpenInUtils

fun LazyListScope.rankList(
    subjectCollection: SubjectCollection,
    subjectCollectionItems: LazyPagingItems<SubjectWithRankAndInterest<*>>,
    isLoggedIn: Boolean,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onMarkClick: (SubjectWithRankAndInterest<*>) -> Unit,
) {
    item(key = "rank_list_header") {
        Text(
            text = subjectCollection.title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
    }

    if (subjectCollectionItems.loadState.refresh is LoadState.Error) {
        val errorState = subjectCollectionItems.loadState.refresh as LoadState.Error
        item(key = "rank_list_error") {
            SectionErrorWithRetry(
                message = errorState.toUiMessage().getString(),
                onRetryClick = { subjectCollectionItems.retry() }
            )
        }
    }

    items(
        count = subjectCollectionItems.itemCount,
        key = subjectCollectionItems.itemKey { it.subject.id }) { index ->
        val context = LocalContext.current
        val subject = subjectCollectionItems[index] ?: return@items
        SubjectItem(
            basicContent = {
                SubjectItemBasicContent(subject = subject.subject)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            onClick = {
                when (subject.subject) {
                    is Movie -> {
                        onMovieClick(subject.subject.id)
                    }

                    is Tv -> {
                        onTvClick(subject.subject.id)
                    }

                    is Book -> {
                        onBookClick(subject.subject.id)
                    }

                    is Subject.Unsupported -> {
                        OpenInUtils.openInDouban(context, subject.subject.uri)
                    }
                }

            },
            rankContent = {
                SubjectItemRank(rankValue = subject.rankValue, listSize = subjectCollection.total)
            },
            interestButton = when (isLoggedIn) {
                true -> {
                    {
                        SubjectSimpleInterestButton(
                            subjectType = subject.type,
                            interest = subject.interest,
                            onMarkClick = {
                                onMarkClick(subject)
                            }
                        )
                    }
                }

                false -> null
            }
        )
        if (subject.rankValue != subjectCollection.total) {
            HorizontalDivider()
        }
    }

    item(key = "rank_list_append_state") {
        when (val appendState = subjectCollectionItems.loadState.append) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> {
                SectionErrorWithRetry(
                    message = appendState.toUiMessage().getString(),
                    onRetryClick = { subjectCollectionItems.retry() }
                )
            }

            else -> {}
        }
    }
}