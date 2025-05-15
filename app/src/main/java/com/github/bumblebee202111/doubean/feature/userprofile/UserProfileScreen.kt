package com.github.bumblebee202111.doubean.feature.userprofile

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.fangorns.HiddenTypeInProfile
import com.github.bumblebee202111.doubean.model.fangorns.UserDetail
import com.github.bumblebee202111.doubean.model.profile.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.model.profile.ProfileStatItemTypes
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.util.intermediateDateTimeString
import com.github.bumblebee202111.doubean.util.toColorOrPrimary


@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UserProfileScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick,
        onLoginClick = onLoginClick,
        onHiddenTypeClick = viewModel::showInfoMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    uiState: UserProfileUiState,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,
    onHiddenTypeClick: (String) -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val isConsideredCollapsed = scrollBehavior.state.collapsedFraction > 0.5f

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(isConsideredCollapsed, isDarkTheme, window) {
        window?.let { win ->
            val controller = WindowInsetsControllerCompat(win, view)
            if (isDarkTheme) {
                controller.isAppearanceLightStatusBars = false
            } else {
                controller.isAppearanceLightStatusBars = isConsideredCollapsed
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            UserProfileTopAppBar(
                uiState = uiState,
                scrollBehavior = scrollBehavior,
                isConsideredCollapsed = isConsideredCollapsed,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                onHiddenTypeClick = onHiddenTypeClick
            )
        },
        content = { innerPadding ->
            UserProfileContentArea(
                uiState = uiState,
                innerPadding = innerPadding,
                onLoginClick = onLoginClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UserProfileTopAppBar(
    uiState: UserProfileUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    isConsideredCollapsed: Boolean,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHiddenTypeClick: (String) -> Unit,
) {
    val user = uiState.user

    val collapsedContainerColor = MaterialTheme.colorScheme.surface
    val collapsedContentColor = MaterialTheme.colorScheme.onSurface

    val expandedContainerColor = user?.profileBanner?.color.toColorOrPrimary()
    val expandedContentColor = Color.White

    val currentAppBarEffectiveIconColor =
        if (isConsideredCollapsed) collapsedContentColor else expandedContentColor
    TwoRowsTopAppBar(
        title = { expanded ->
            if (user != null) {
                TopAppBarTitleContent(
                    isExpanded = expanded,
                    user = user,
                    communityContribution = uiState.communityContribution,
                    expandedContentColor = expandedContentColor,
                    collapsedContentColor = collapsedContentColor,
                    onHiddenTypeClick = onHiddenTypeClick
                )
            }
        },
        navigationIcon = {
            if (!uiState.isTargetingCurrentUser) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            if (uiState.isTargetingCurrentUser) {
                IconButton(onClick = onSettingsClick) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = expandedContainerColor,
            scrolledContainerColor = collapsedContainerColor,
            navigationIconContentColor = currentAppBarEffectiveIconColor,
            titleContentColor = expandedContentColor,
            actionIconContentColor = currentAppBarEffectiveIconColor
        )
    )
}

@Composable
private fun TopAppBarTitleContent(
    isExpanded: Boolean,
    user: UserDetail?,
    communityContribution: ProfileCommunityContribution?,
    expandedContentColor: Color,
    collapsedContentColor: Color,
    onHiddenTypeClick: (String) -> Unit,
) {
    if (user == null) {
        return
    }

    if (isExpanded) {
        CompositionLocalProvider(LocalContentColor provides expandedContentColor) {
            UserInfo(
                user = user,
                communityContribution = communityContribution,
                onHiddenTypeClick = onHiddenTypeClick
            )
        }
    } else {
        Text(
            text = user.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = collapsedContentColor
        )
    }
}

@Composable
private fun UserProfileContentArea(
    uiState: UserProfileUiState,
    innerPadding: PaddingValues,
    onLoginClick: () -> Unit,
) {

    when {
        uiState.isLoading -> {
            FullScreenCenteredContent(innerPadding) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null -> {
            FullScreenCenteredContent(innerPadding) {
                Text(
                    text = "Error: ${uiState.errorMessage.getString()}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )

            }
        }

        uiState.user != null -> {
            UserProfileMainContent(
                user = uiState.user,
                contentPadding = innerPadding
            )
        }

        uiState.isTargetingCurrentUser && !uiState.isLoggedIn -> {
            FullScreenCenteredContent(innerPadding) {
                Button(onClick = onLoginClick) {
                    Text(text = stringResource(R.string.title_login))
                }
            }
        }

    }
}

@Composable
private fun FullScreenCenteredContent(
    paddingValues: PaddingValues,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun UserProfileMainContent(user: UserDetail, contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

    }
}

@Composable
private fun UserInfo(
    user: UserDetail,
    communityContribution: ProfileCommunityContribution?,
    onHiddenTypeClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 12.dp)
            .padding(bottom = 24.dp)
    ) {
        UserProfileImage(url = user.avatar, size = 64.dp)
        Spacer(Modifier.height(2.dp))
        Text(text = user.name, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(2.dp))

        val idText = stringResource(R.string.user_info_id, user.uid)
        val ipText =
            user.ipLocation?.takeIf { it.isNotBlank() }
                ?.let { stringResource(R.string.user_info_ip, it) }
        val userInfoMore = listOfNotNull(idText, ipText).joinToString(" / ")
        UserInfoText(text = userInfoMore)
        UserInfoText(
            text = stringResource(
                R.string.user_info_register_time,
                user.registerTime.intermediateDateTimeString()
            )
        )
        user.location?.takeIf { it.isNotBlank() }?.let {
            UserInfoText(text = stringResource(R.string.user_info_location, it))
        }
        user.hometown?.takeIf { it.isNotBlank() }?.let {
            UserInfoText(text = stringResource(R.string.user_info_hometown, it))
        }

        if (user.hasCommunityContribution && communityContribution != null && communityContribution.items.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.about_contribution),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(communityContribution.items, key = { it.type }) { statItem ->
                    val isDoulistType = statItem.type == ProfileStatItemTypes.OWNED_DOULIST
                    val isDoulistHidden =
                        user.hiddenTypesInProfile.contains(HiddenTypeInProfile.DOULIST)
                    val canNavigateDoulist = isDoulistType && !isDoulistHidden
                    val showHidingReasonOnClick =
                        isDoulistType && isDoulistHidden && user.profileHidingReason.isNotBlank()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .then(
                                when {
                                    canNavigateDoulist -> Modifier.clickable {

                                    }

                                    showHidingReasonOnClick -> Modifier.clickable {
                                        onHiddenTypeClick(user.profileHidingReason)
                                    }

                                    else -> Modifier
                                }
                            )
                            .padding(4.dp)
                    ) {

                        Text(
                            text = statItem.total.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = statItem.title,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        user.intro.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(16.dp))
            UserInfoText(text = it, maxLines = Int.MAX_VALUE)
        }
    }
}

@Composable
private fun UserInfoText(
    text: String,
    maxLines: Int = 1,
) {
    Text(
        text = text,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        style = MaterialTheme.typography.bodySmall
    )
}