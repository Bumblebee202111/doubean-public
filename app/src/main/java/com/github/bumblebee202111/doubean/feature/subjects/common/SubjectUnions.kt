package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.model.subjects.SubjectWithInterest
import kotlinx.coroutines.launch

@Composable
fun SubjectUnions(
    module: SubjectModule.SubjectUnions,
    onSubjectClick: (SubjectWithInterest<*>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        module.subjectUnions.size
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        module.subjectUnions.forEachIndexed { index, union ->
            val isSelected = pagerState.currentPage == index
            val textColor = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.6f)
            Text(
                text = union.title,
                color = textColor,
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                style = MaterialTheme.typography.titleMedium
            )

            if (index != module.subjectUnions.lastIndex) {
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray.copy(alpha = 0.2f),
                )
            }
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.padding(top = 8.dp),
        userScrollEnabled = false,
        key = { module.subjectUnions[it].title }
    ) { page ->
        val items = module.subjectUnions[page].items
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(items = items, key = { it.id }) { subject ->
                Box(Modifier.clickable { onSubjectClick(subject) }) {
                    SimpleSubjectRowItemContent(subject.subject)
                }
            }
        }
    }
}
