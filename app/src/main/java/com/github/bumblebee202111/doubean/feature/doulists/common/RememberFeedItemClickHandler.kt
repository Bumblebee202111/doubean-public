package com.github.bumblebee202111.doubean.feature.doulists.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.model.common.BaseFeedableItem
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.common.SubjectFeedContent
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.util.OpenInUtils

@Composable
fun rememberFeedItemClickHandler(
    onTopicClick: (String) -> Unit,
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
): (FeedItem<*>) -> Unit {
    val context = LocalContext.current
    return remember(context, onTopicClick, onSubjectClick) {
        { feedItem ->
            val handledInternally = attemptInternalNavigation(
                feedItem = feedItem,
                onTopicClick = onTopicClick,
                onSubjectClick = onSubjectClick
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
    onSubjectClick: (id: String, type: SubjectType) -> Unit,
): Boolean {
    return when (feedItem.type) {
        BaseFeedableItem.TYPE_TOPIC -> {
            onTopicClick(feedItem.uri)
            true
        }

        BaseFeedableItem.TYPE_MOVIE, BaseFeedableItem.TYPE_BOOK -> {
            when (val content = feedItem.content) {
                !is SubjectFeedContent -> false
                else -> {
                    val subject = content.subject.subject
                    val subjectId = subject.id
                    val type = content.subject.type
                    if (type != SubjectType.UNSUPPORTED) {
                        onSubjectClick(subjectId, type)
                        true
                    } else {
                        false
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
