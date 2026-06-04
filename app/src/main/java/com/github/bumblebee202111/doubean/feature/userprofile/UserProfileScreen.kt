package com.github.bumblebee202111.doubean.feature.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.fangorns.HiddenTypeInProfile
import com.github.bumblebee202111.doubean.model.fangorns.UserDetail
import com.github.bumblebee202111.doubean.model.profile.ProfileCommunityContribution
import com.github.bumblebee202111.doubean.model.profile.ProfileStatItemTypes
import com.github.bumblebee202111.doubean.model.subjects.MySubject
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.common.ApplyStatusBarIconAppearance
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanButton
import com.github.bumblebee202111.doubean.ui.component.FullScreenCenteredContent
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.UserProfileImage
import com.github.bumblebee202111.doubean.ui.component.doubeanExpandedTopBarContentPadding
import com.github.bumblebee202111.doubean.util.DateTimeStyle
import com.github.bumblebee202111.doubean.util.toColorOrPrimary
import com.github.bumblebee202111.doubean.util.toRelativeString


@Composable
fun UserProfileScreen(
    onStatItemUriClick: (uri: String) -> Boolean,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
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
        onSubjectStatusClick = onSubjectStatusClick,
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
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
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
                onSubjectStatusClick = onSubjectStatusClick,
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
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
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
                uiState = uiState,
                onSubjectStatusClick = onSubjectStatusClick,
                contentPadding = innerPadding
            )
        }

        uiState.isTargetingCurrentUser && !uiState.isLoggedIn -> {
            FullScreenCenteredContent(innerPadding) {
                DoubeanButton(onClick = onLoginClick) {
                    Text(text = stringResource(R.string.title_login))
                }
            }
        }

    }
}

@Composable
private fun UserProfileMainContent(
    user: UserDetail,
    uiState: UserProfileUiState,
    onSubjectStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (uiState.profileSubjects != null) {
            item {
                ProfileSubjectsSection(
                    subjects = uiState.profileSubjects,
                    isCurrentUser = uiState.isTargetingCurrentUser,
                    onSubjectClick = { type ->
                        onSubjectStatusClick(user.id, type)
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileSubjectsSection(
    subjects: List<MySubject>,
    isCurrentUser: Boolean,
    onSubjectClick: (SubjectType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                MaterialTheme.shapes.medium
            )
            .padding(vertical = 16.dp)
    ) {
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_hitmap),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(12.dp))

        if (subjects.isEmpty() && isCurrentUser) {
            Text(
                text = stringResource(R.string.login_prompt_mark_subject), 
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects, key = { it.type.name }) { subject ->
                    ProfileSubjectCard(
                        subject = subject,
                        onClick = { onSubjectClick(subject.type) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSubjectCard(subject: MySubject, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.width(140.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            
            val covers = subject.interests.mapNotNull { it.subjectCoverUrl }.take(3)
            if (covers.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy((-16).dp)) {
                    covers.forEachIndexed { index, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(
                                    width = 48.dp,
                                    height = 68.dp
                                ) 
                                .zIndex(covers.size - index.toFloat())
                                .clip(MaterialTheme.shapes.extraSmall)
                                .border(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    shape = MaterialTheme.shapes.extraSmall
                                ),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                subject.interests.forEach { interest ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = interest.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = interest.count.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
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