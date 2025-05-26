package com.github.bumblebee202111.doubean.feature.doulists.doulist


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.common.DouListPostItem
import com.github.bumblebee202111.doubean.model.common.FeedItem
import com.github.bumblebee202111.doubean.model.subjects.MarkableSubject
import com.github.bumblebee202111.doubean.ui.common.feed.FeedItem
import com.github.bumblebee202111.doubean.util.abbreviatedDateTimeString

@Composable
fun DouListPostItem(
    postItem: DouListPostItem,
    isLoggedIn: Boolean,
    onItemClick: (FeedItem<*>) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onMarkSubject: (MarkableSubject) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(postItem.feedItem)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        FeedItem(
            feedItem = postItem.feedItem,
            isLoggedIn = isLoggedIn,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onMarkSubject = onMarkSubject,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.collect_time_new) + postItem.collectionTime.abbreviatedDateTimeString(
                    LocalContext.current
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (postItem.collectionReason.isNotBlank()) {
                Text(
                    text = postItem.collectionReason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

}