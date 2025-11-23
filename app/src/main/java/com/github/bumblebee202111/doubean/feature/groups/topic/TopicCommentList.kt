package com.github.bumblebee202111.doubean.feature.groups.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.groups.TopicComment
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
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
    when (val refreshState =
        comments.loadState.refresh) {
        is LoadState.Error -> {
            item(key = "all_comments_error") {
                SectionErrorWithRetry(
                    message = refreshState.toUiMessage()
                        .getString(),
                    onRetryClick = { comments.retry() }
                )
            }
        }

        else -> {
            items(
                count = comments.itemCount,
                key = comments.itemKey { it.id },
                contentType = comments.itemContentType { "TopicComment" }) { index ->
                comments[index]?.let {
                    TopicComment(
                        comment = it,
                        topic = topic,
                        onUserClick = onUserClick,
                        onImageClick = onImageClick
                    )
                    if (index < comments.itemCount - 1)
                        HorizontalDivider(thickness = 1.dp)
                }
            }

            item(key = "append_state") {
                when (val appendState = comments.loadState.append) {
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
                        val errorMessage = appendState.toUiMessage()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = errorMessage.getString(),
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { comments.retry() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

}