package com.github.bumblebee202111.doubean.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.doulists.ItemDouList
import com.github.bumblebee202111.doubean.ui.component.SelectionDialog

data class CollectDialogUiState(
    val isLoading: Boolean = false,
    val douLists: List<ItemDouList> = emptyList(),
)

@Composable
fun DouListDialog(
    uiState: CollectDialogUiState,
    onDismissRequest: () -> Unit,
    onDouListClick: (douList: ItemDouList) -> Unit,
) {
    SelectionDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.title_collect_to_doulist),
        modifier = Modifier.heightIn(min = 200.dp, max = 500.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.douLists, key = { it.id }) { douList ->
                    CollectedDouListItem(
                        douList = douList,
                        onClick = { onDouListClick(douList) }
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun CollectedDouListItem(
    douList: ItemDouList,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val imageModifier = if (douList.isMergedCover == true) {
            Modifier.size(56.dp)
        } else {
            Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(douList.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = douList.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.doulist_content_count, douList.itemCount),
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (douList.isCollected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Collected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}