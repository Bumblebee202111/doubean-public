package com.github.bumblebee202111.doubean.feature.subjects.shared

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.model.Book
import com.github.bumblebee202111.doubean.model.Movie
import com.github.bumblebee202111.doubean.model.Subject
import com.github.bumblebee202111.doubean.model.SubjectCollection
import com.github.bumblebee202111.doubean.model.SubjectWithRankAndInterest
import com.github.bumblebee202111.doubean.model.Tv
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
    item {
        Text(
            text = subjectCollection.title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.size(4.dp))
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
}