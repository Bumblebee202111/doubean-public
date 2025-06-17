package com.github.bumblebee202111.doubean.feature.doulists.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.model.common.BaseFeedableItem
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.common.SubjectFeedContent
import com.github.bumblebee202111.doubean.model.subjects.Book
import com.github.bumblebee202111.doubean.model.subjects.Movie
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.Tv
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun rememberFeedItemClickHandler(
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
): (FeedItem<*>) -> Unit {
    val context = LocalContext.current
    return remember(context, onTopicClick, onBookClick, onMovieClick, onTvClick) {
        { feedItem ->
            val handledInternally = attemptInternalNavigation(
                feedItem = feedItem,
                onTopicClick = onTopicClick,
                onBookClick = onBookClick,
                onMovieClick = onMovieClick,
                onTvClick = onTvClick
            )

            if (!handledInternally) {
                val openedInDoubanApp = OpenInUtils.openInDouban(context, feedItem.uri).isSuccess
                if (!openedInDoubanApp) {
                    OpenInUtils.openInBrowser(context, feedItem.url)
                }
            }
        }
    }
}

private fun attemptInternalNavigation(
    feedItem: FeedItem<*>,
    onTopicClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onTvClick: (String) -> Unit,
): Boolean {
    return when (feedItem.type) {
        BaseFeedableItem.TYPE_TOPIC -> {
            onTopicClick(feedItem.id)
            true
        }

        BaseFeedableItem.TYPE_MOVIE, BaseFeedableItem.TYPE_BOOK -> {
            when (val content = feedItem.content) {
                !is SubjectFeedContent -> false
                else -> {
                    val subject = content.subject.subject
                    val subjectId = subject.id
                    when (subject) {
                        is Book -> {
                            onBookClick(subjectId)
                            true
                        }

                        is Movie -> {
                            onMovieClick(subjectId)
                            true
                        }

                        is Tv -> {
                            onTvClick(subjectId)
                            true
                        }

                        is Subject.Unsupported -> { 
                            false
                        }
                    }
                }
            }
        }

        BaseFeedableItem.TYPE_REVIEW -> {
            false
        }

        else -> false
    }
}