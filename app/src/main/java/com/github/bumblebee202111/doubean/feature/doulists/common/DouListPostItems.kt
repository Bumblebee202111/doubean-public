package com.github.bumblebee202111.doubean.feature.doulists.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject

fun LazyListScope.douListPostItems(
    items: List<DouListPostItem>,
    isLoggedIn: Boolean,
    isLoadingMore: Boolean,
    onItemClick: (FeedItem<*>) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
    onDouListClick: ((douListId: String) -> Unit)? = null,
) {
    if (items.isEmpty() && !isLoadingMore) {
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxHeight(0.5f)
                    .fillParentMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.empty_content_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    } else {
        items(items, key = { it.feedItem.uid }) { postItem ->
            DouListPostItem(
                postItem = postItem,
                isLoggedIn = isLoggedIn,
                onItemClick = onItemClick,
                onUserClick = onUserClick,
                onImageClick = onImageClick,
                onMarkSubject = onMarkSubject,
                onDouListClick = onDouListClick
            )
            if (items.last() != postItem || isLoadingMore) {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }

    if (isLoadingMore) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}