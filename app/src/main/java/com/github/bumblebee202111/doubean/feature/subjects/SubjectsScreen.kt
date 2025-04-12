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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.books.BooksScreen
import com.github.bumblebee202111.doubean.feature.subjects.movies.MoviesScreen
import com.github.bumblebee202111.doubean.feature.subjects.tvs.TvsScreen
import com.github.bumblebee202111.doubean.model.User
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import kotlinx.coroutines.launch

@Composable
fun SubjectsScreen(
    onAvatarClick: () -> Unit,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    viewModel: SubjectsViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String) -> Unit,
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    SubjectsScreen(
        currentUser = currentUser,
        onAvatarClick = onAvatarClick,
        onSubjectStatusClick = onSubjectStatusClick,
        onLoginClick = onLoginClick,
        onSearchClick = onSearchClick,
        onRankListClick = onRankListClick,
        onMovieClick = onMovieClick,
        onTvClick = onTvClick,
        onBookClick = onBookClick,
        onShowSnackbar = onShowSnackbar
    )
}

@Composable
fun SubjectsScreen(
    currentUser: User?,
    onAvatarClick: () -> Unit,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {
    Scaffold(
        topBar = {
            SubjectsAppBar(
                currentUser = currentUser,
                onAvatarClick = onAvatarClick,
                onSearchClick = onSearchClick
            )
        }
    ) { innerPadding ->
        val pagerState = rememberPagerState {
            SubjectsTab.entries.size
        }
        Column(
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ) {
            SubjectsTabRow(pagerState = pagerState)
            SubjectsPager(
                state = pagerState,
                onSubjectStatusClick = onSubjectStatusClick,
                onLoginClick = onLoginClick,
                onRankListClick = onRankListClick,
                onMovieClick = onMovieClick,
                onTvClick = onTvClick,
                onBookClick = onBookClick,
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectsAppBar(
    currentUser: User?,
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    DoubeanTopAppBar(
        navigationIcon = {
            IconButton(onClick = onAvatarClick) {
                if (currentUser == null) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                } else {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = null
                    )
                }
            }
        },
        title = {},
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun SubjectsTabRow(pagerState: PagerState) {
    PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
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
private fun SubjectsPager(
    state: PagerState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
    onRankListClick: (collectionId: String) -> Unit,
    onMovieClick: (movieId: String) -> Unit,
    onTvClick: (tvId: String) -> Unit,
    onBookClick: (bookId: String) -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
) {
    HorizontalPager(state = state, userScrollEnabled = false) { page ->
        val modifier = Modifier.fillMaxSize()
        when (SubjectsTab.entries[page]) {
            SubjectsTab.MOVIES -> {
                MoviesScreen(
                    onLoginClick = onLoginClick,
                    onSubjectStatusClick = onSubjectStatusClick,
                    onRankListClick = onRankListClick,
                    onMovieClick = onMovieClick,
                    onShowSnackbar = onShowSnackbar,
                    modifier = modifier
                )
            }

            SubjectsTab.TVS -> {
                TvsScreen(
                    onSubjectStatusClick = onSubjectStatusClick,
                    onLoginClick = onLoginClick,
                    onRankListClick = onRankListClick,
                    onTvClick = onTvClick,
                    onShowSnackbar = onShowSnackbar,
                    modifier = modifier,
                )
            }

            SubjectsTab.BOOKS -> {
                BooksScreen(
                    onSubjectStatusClick = onSubjectStatusClick,
                    onLoginClick = onLoginClick,
                    onRankListClick = onRankListClick,
                    onBookClick = onBookClick,
                    onShowSnackbar = onShowSnackbar,
                    modifier = modifier,
                )
            }
        }
    }
}

private enum class SubjectsTab(
    val textResId: Int,
    val iconVector: ImageVector,
) {
    MOVIES(
        textResId = R.string.title_movies,
        iconVector = Icons.Filled.Movie
    ),
    TVS(
        textResId = R.string.title_tvs,
        iconVector = Icons.Filled.Tv
    ),
    BOOKS(
        textResId = R.string.title_books,
        iconVector = Icons.Filled.Book
    ),
}