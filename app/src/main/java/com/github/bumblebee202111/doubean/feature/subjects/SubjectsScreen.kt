package com.github.bumblebee202111.doubean.feature.subjects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.movies.MoviesScreen
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import kotlinx.coroutines.launch

@Composable
fun SubjectsScreen(onSettingsClick: () -> Unit) {
    Scaffold(
        topBar = {
            SubjectsAppBar(onSettingsClick)
        }
    ) {
        val pagerState = rememberPagerState {
            SubjectsTab.entries.size
        }
        Column(
            modifier = Modifier.padding(
                start = it.calculateStartPadding(LayoutDirection.Ltr),
                top = it.calculateTopPadding(),
                end = it.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            SubjectsTabRow(pagerState = pagerState)
            SubjectsPager(state = pagerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectsAppBar(onSettingsClick: () -> Unit) {
    DoubeanTopAppBar(
        title = {},
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun SubjectsTabRow(pagerState: PagerState) {

    TabRow(selectedTabIndex = pagerState.currentPage) {
        SubjectsTab.entries.forEachIndexed { index, tab ->

            val coroutineScope = rememberCoroutineScope()
            val text = stringResource(id = tab.textResId)
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                text = {
                    Text(text = text)
                },
                icon = {
                    Icon(
                        imageVector = tab.iconVector,
                        contentDescription = text
                    )
                })
        }
    }
}

@Composable
private fun SubjectsPager(state: PagerState) {
    HorizontalPager(state = state) { page ->
        SubjectsTab.entries[page].content(Modifier.fillMaxSize())
    }
}

private enum class SubjectsTab(
    val textResId: Int,
    val iconVector: ImageVector,
    val content: @Composable (modifier: Modifier) -> Unit,
) {
    MOVIES(
        textResId = R.string.title_movies,
        iconVector = Icons.Filled.Movie,
        content = {
            MoviesScreen(modifier = it)
        }
    ),
//    BOOKS(
//        titleResId = R.string.title_books,
//        iconVector = Icons.Filled.Book,
//        content = {
//            BooksScreen(modifier = Modifier.fillMaxSize())
//        }
//    ),
}