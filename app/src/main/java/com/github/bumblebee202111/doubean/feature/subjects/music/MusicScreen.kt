package com.github.bumblebee202111.doubean.feature.subjects.music

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.feature.subjects.SubjectModulesUiState
import com.github.bumblebee202111.doubean.feature.subjects.common.SimpleSubjectItemContent
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectModuleTitle
import com.github.bumblebee202111.doubean.model.subjects.SubjectModule
import com.github.bumblebee202111.doubean.ui.component.SectionErrorWithRetry
import kotlinx.coroutines.launch

@Composable
fun MusicScreen(
    onRankListClick: (collectionId: String) -> Unit,
    onMusicClick: (musicId: String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: MusicViewModel = hiltViewModel(),
) {
    val modulesUiState by viewModel.modulesUiState.collectAsStateWithLifecycle()
    MusicScreen(
        modifier = modifier,
        modulesUiState = modulesUiState,
        contentPadding = contentPadding,
        onRankListClick = onRankListClick,
        onMusicClick = onMusicClick,
        onRetryClick = viewModel::retry
    )
}

@Composable
fun MusicScreen(
    modifier: Modifier = Modifier,
    modulesUiState: SubjectModulesUiState,
    contentPadding: PaddingValues = PaddingValues(),
    onRankListClick: (collectionId: String) -> Unit,
    onMusicClick: (musicId: String) -> Unit,
    onRetryClick: () -> Unit,
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        when (modulesUiState) {
            is SubjectModulesUiState.Success -> {
                modulesUiState.modules.forEach { module ->
                    when (module) {
                        is SubjectModule.SubjectUnion -> {
                            item {
                                MusicSubjectUnionModule(
                                    module = module,
                                    onMusicClick = onMusicClick
                                )
                            }
                        }

                        is SubjectModule.CollectionBoard -> {
                            item {
                                MusicCollectionBoardModule(
                                    module = module,
                                    onMusicClick = onMusicClick
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }

            SubjectModulesUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is SubjectModulesUiState.Error -> {
                item {
                    SectionErrorWithRetry(
                        message = modulesUiState.message.getString(),
                        onRetryClick = onRetryClick
                    )
                }
            }

        }
    }
}

@Composable
private fun MusicCollectionBoardModule(
    module: SubjectModule.CollectionBoard,
    onMusicClick: (String) -> Unit,
) {
    Column {
        SubjectModuleTitle(title = module.title)
        Spacer(modifier = Modifier.height(12.dp))

        val chunkedItems = module.items.chunked(2)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = chunkedItems, key = { it.first().subject.id }) { columnItems ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    columnItems.forEach { subjectWithInterest ->
                        Box(Modifier.clickable { onMusicClick(subjectWithInterest.subject.id) }) {
                            SimpleSubjectItemContent(subjectWithInterest.subject)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicSubjectUnionModule(
    module: SubjectModule.SubjectUnion,
    onMusicClick: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { module.collections.size }

    Column {
        SubjectModuleTitle(title = module.title)
        Spacer(modifier = Modifier.height(12.dp))

        SecondaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 16.dp,
            divider = {},
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            module.collections.forEachIndexed { index, collection ->
                val isSelected = pagerState.currentPage == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = collection.title,
                            style = if (isSelected) MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ) else MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            key = { module.collections[it].title }
        ) { page ->
            val chunkedItems = module.collections[page].items.chunked(2)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = chunkedItems, key = { it.first().subject.id }) { columnItems ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        columnItems.forEach { subjectWithInterest ->
                            Box(Modifier.clickable { onMusicClick(subjectWithInterest.subject.id) }) {
                                SimpleSubjectItemContent(subjectWithInterest.subject)
                            }
                        }
                    }
                }
            }
        }
    }
}