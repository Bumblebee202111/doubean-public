package com.github.bumblebee202111.doubean.feature.groups.topic

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.model.group.TopicComment
import com.github.bumblebee202111.doubean.model.group.TopicDetail
import com.github.bumblebee202111.doubean.ui.component.PagingAppendFooter
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry
import com.github.bumblebee202111.doubean.ui.util.toUiMessage

fun LazyListScope.popularComments(
    comments: List<TopicComment>,
    topic: TopicDetail,
    onUserClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
) {
    items(
        count = comments.size,
        key = { comments[it].id },
        contentType = { "TopicComment" }) { index ->
        TopicComment(
            comment = comments[index],
            topic = topic,
            onUserClick = onUserClick,
            onImageClick = onImageClick
        )
        if (index < comments.size - 1)
            HorizontalDivider(thickness = 1.dp)
    }
}

fun LazyListScope.allComments(
    comments: LazyPagingItems<TopicComment>,
    topic: TopicDetail,
    onUserClick: (id: String) -> Unit,
    onImageClick: (url: String) -> Unit,
) {
    when (val refreshState = comments.loadState.refresh) {
        is LoadState.Error -> {
            item(key = "all_comments_error") {
                SectionErrorWithRetry(
                    message = refreshState.toUiMessage().getString(),
                    onRetryClick = { comments.retry() }
                )
            }
        }

        else -> {
            items(
                count = comments.itemCount,
                key = comments.itemKey { it.id },
                contentType = comments.itemContentType { "TopicComment" }) { index ->
                val comment = comments[index]
                if (comment != null) {
                    TopicComment(
                        comment = comment,
                        topic = topic,
                        onUserClick = onUserClick,
                        onImageClick = onImageClick
                    )
                } else {
                    TopicCommentPlaceholder()
                }
                if (index < comments.itemCount - 1)
                    HorizontalDivider(thickness = 1.dp)
            }

            item(key = "append_state") {
                PagingAppendFooter(
                    loadState = comments.loadState.append,
                    onRetryClick = { comments.retry() }
                )
            }
        }
    }
}