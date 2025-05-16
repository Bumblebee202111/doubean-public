package com.github.bumblebee202111.doubean.feature.doulists.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.doulists.DouList

@Composable
fun UserDouLists(
    douLists: List<DouList>,
    onItemClick: (douListId: String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(douLists, key = { it.id }) { douList ->
            DouListItem(
                douList = douList,
                onItemClick = { onItemClick(douList.id) }
            )
        }
    }
}