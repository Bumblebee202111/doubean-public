package com.github.bumblebee202111.doubean.feature.userprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.fangorns.HiddenTypeInProfile
import com.github.bumblebee202111.doubean.model.fangorns.UserDetail
import com.github.bumblebee202111.doubean.model.profile.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.model.profile.ProfileStatItemTypes
import com.github.bumblebee202111.doubean.ui.common.ApplyStatusBarIconAppearance
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.ui.component.doubeanExpandedTopBarContentPadding
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import com.github.bumblebee202111.doubean.util.toRelativeString

// UserProfileFragment/NewUserProfileActivity/UserProfileBioActivity/UserInfoActivity
@Composable
fun UserProfileScreen(
    onStatItemUriClick: (uri: String) -> Boolean,
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
        onHiddenTypeClick = viewModel::showInfoMessage,
        onStatItemUriClick = onStatItemUriClick,
        onRetryClick = viewModel::retry
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
    onStatItemUriClick: (uri: String) -> Boolean,
    onRetryClick: () -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val isConsideredCollapsed = scrollBehavior.state.collapsedFraction > 0.5f

    val isAppInDarkTheme = isSystemInDarkTheme()

    val useLightIcons: Boolean = if (isAppInDarkTheme) {
        true
    } else {
        !isConsideredCollapsed
    }

    ApplyStatusBarIconAppearance(useLightIcons = useLightIcons)

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
                onHiddenTypeClick = onHiddenTypeClick,
                onStatItemUriClick = onStatItemUriClick
            )
        },
        content = { innerPadding ->
            UserProfileContentArea(
                uiState = uiState,
                innerPadding = innerPadding,
                onLoginClick = onLoginClick,
                onRetryClick = onRetryClick
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
    onStatItemUriClick: (uri: String) -> Boolean,
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
                    onHiddenTypeClick = onHiddenTypeClick,
                    onStatItemUriClick = onStatItemUriClick
                )
            }
        },
        navigationIcon = {
            if (!uiState.isTargetingCurrentUser) {
                BackButton(onClick = onBackClick)
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
    onStatItemUriClick: (uri: String) -> Boolean,
) {
    if (user == null) {
        return
    }

    if (isExpanded) {
        CompositionLocalProvider(LocalContentColor provides expandedContentColor) {
            UserInfo(
                user = user,
                communityContribution = communityContribution,
                onHiddenTypeClick = onHiddenTypeClick,
                onStatItemUriClick = onStatItemUriClick
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
    onRetryClick: () -> Unit,
) {

    when {
        uiState.isLoading -> {
            FullScreenLoadingIndicator(contentPadding = innerPadding)
        }

        uiState.errorMessage != null -> {
            FullScreenErrorWithRetry(
                message = uiState.errorMessage.getString(),
                onRetryClick = onRetryClick,
                contentPadding = innerPadding
            )
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
private fun UserProfileMainContent(user: UserDetail, contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TODO
    }
}

@Composable
private fun UserInfo(
    user: UserDetail,
    communityContribution: ProfileCommunityContribution?,
    onHiddenTypeClick: (String) -> Unit,
    onStatItemUriClick: (uri: String) -> Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(doubeanExpandedTopBarContentPadding)
    ) {
        UserProfileImage(url = user.avatar, size = 64.dp)
        Spacer(Modifier.height(2.dp))
        Text(text = user.name, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(2.dp))
        // userInfoMore
        val idText = stringResource(R.string.user_info_id, user.uid)
        val ipText =
            user.ipLocation?.takeIf { it.isNotBlank() }
                ?.let { stringResource(R.string.user_info_ip, it) }
        val userInfoMore = listOfNotNull(idText, ipText).joinToString(" / ")
        UserInfoText(text = userInfoMore)
        UserInfoText(
            text = stringResource(
                R.string.user_info_register_time,
                user.registerTime.toRelativeString(style = DateTimeStyle.INTERMEDIATE)
            )
        )
        user.location?.takeIf { it.isNotBlank() }?.let {
            UserInfoText(text = stringResource(R.string.user_info_location, it))
        }
        user.hometown?.takeIf { it.isNotBlank() }?.let {
            UserInfoText(text = stringResource(R.string.user_info_hometown, it))
        }
        // infoContainer (+ about me)
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
                                        onStatItemUriClick(statItem.uri)
                                    }

                                    showHidingReasonOnClick -> Modifier.clickable {
                                        onHiddenTypeClick(user.profileHidingReason)
                                    }

                                    else -> Modifier
                                }
                            )
                            .padding(4.dp)
                    ) {
                        // count
                        Text(
                            text = statItem.total.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        // contributionName
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