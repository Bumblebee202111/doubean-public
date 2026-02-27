package com.github.bumblebee202111.doubean.navigation

import androidx.navigation3.runtime.entryProvider
import com.github.bumblebee202111.doubean.feature.doulists.createddoulists.navigation.createdDouListsEntry
import com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation.douListEntry
import com.github.bumblebee202111.doubean.feature.doulists.doulist.navigation.navigateToDouList
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.groupDetailEntry
import com.github.bumblebee202111.doubean.feature.groups.groupdetail.navigation.navigateToGroup
import com.github.bumblebee202111.doubean.feature.groups.home.navigation.groupsHomeEntry
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.navigateToReshareStatuses
import com.github.bumblebee202111.doubean.feature.groups.resharestatuses.navigation.reshareStatusesEntry
import com.github.bumblebee202111.doubean.feature.groups.search.navigation.groupsSearchEntry
import com.github.bumblebee202111.doubean.feature.groups.search.navigation.navigateToSearch
import com.github.bumblebee202111.doubean.feature.groups.topic.navigation.topicEntry
import com.github.bumblebee202111.doubean.feature.groups.webView.navigation.navigateToWebView
import com.github.bumblebee202111.doubean.feature.groups.webView.navigation.webViewEntry
import com.github.bumblebee202111.doubean.feature.imageviewer.navigation.imageViewerEntry
import com.github.bumblebee202111.doubean.feature.imageviewer.navigation.navigateToImageViewer
import com.github.bumblebee202111.doubean.feature.login.navigation.LoginNavKey
import com.github.bumblebee202111.doubean.feature.login.navigation.loginEntry
import com.github.bumblebee202111.doubean.feature.login.navigation.navigateToLogin
import com.github.bumblebee202111.doubean.feature.login.navigation.verifyPhoneEntry
import com.github.bumblebee202111.doubean.feature.mydoulists.navigation.myDouListsEntry
import com.github.bumblebee202111.doubean.feature.notifications.navigation.navigateToNotifications
import com.github.bumblebee202111.doubean.feature.notifications.navigation.notificationsEntry
import com.github.bumblebee202111.doubean.feature.settings.navigation.groupDefaultNotificationsPreferencesSettingsEntry
import com.github.bumblebee202111.doubean.feature.settings.navigation.navigateToGroupDefaultNotificationsPreferencesSettings
import com.github.bumblebee202111.doubean.feature.settings.navigation.navigateToSettings
import com.github.bumblebee202111.doubean.feature.settings.navigation.settingsEntry
import com.github.bumblebee202111.doubean.feature.statuses.navigation.statusesEntry
import com.github.bumblebee202111.doubean.feature.subjects.book.navigation.bookEntry
import com.github.bumblebee202111.doubean.feature.subjects.book.navigation.navigateToBook
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.interestsEntry
import com.github.bumblebee202111.doubean.feature.subjects.interests.navigation.navigateToInterests
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.movieEntry
import com.github.bumblebee202111.doubean.feature.subjects.movie.navigation.navigateToMovie
import com.github.bumblebee202111.doubean.feature.subjects.navigation.subjectsEntry
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.navigateToRankList
import com.github.bumblebee202111.doubean.feature.subjects.ranklist.navigation.rankListEntry
import com.github.bumblebee202111.doubean.feature.subjects.search.navigation.navigateToSearchSubjects
import com.github.bumblebee202111.doubean.feature.subjects.search.navigation.searchSubjectsEntry
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.navigateToTv
import com.github.bumblebee202111.doubean.feature.subjects.tv.navigation.tvEntry
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.navigateToUserProfile
import com.github.bumblebee202111.doubean.feature.userprofile.navigation.userProfileEntry

fun createDoubeanEntryProvider(
    navigator: Navigator,
    onAvatarClick: () -> Unit,
) = entryProvider {

    val navigateToUri = { uriString: String ->
        val key = uriString.toNavKeyOrNull()
        if (key != null) {
            navigator.navigate(key)
            true
        } else {
            false
        }
    }

    
    statusesEntry(onAvatarClick = onAvatarClick)

    subjectsEntry(
        onAvatarClick = onAvatarClick,
        onSubjectStatusClick = navigator::navigateToInterests,
        onLoginClick = navigator::navigateToLogin,
        onSearchClick = navigator::navigateToSearchSubjects,
        onRankListClick = navigator::navigateToRankList,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook
    )

    groupsHomeEntry(
        onAvatarClick = onAvatarClick,
        onSearchClick = navigator::navigateToSearch,
        onNotificationsClick = navigator::navigateToNotifications,
        onGroupClick = navigator::navigateToGroup,
        onTopicClick = { navigateToUri(it) }
    )

    
    userProfileEntry(
        onBackClick = navigator::goBack,
        onSettingsClick = navigator::navigateToSettings,
        onLoginClick = navigator::navigateToLogin,
        onStatItemUriClick = navigateToUri
    )

    
    groupsSearchEntry(
        onGroupClick = navigator::navigateToGroup,
        onBackClick = navigator::goBack
    )
    notificationsEntry(
        onBackClick = navigator::goBack,
        onTopicClick = { navigateToUri(it) },
        onGroupClick = { groupId -> navigator.navigateToGroup(groupId) },
        onSettingsClick = navigator::navigateToSettings
    )
    groupDetailEntry(
        onBackClick = navigator::goBack,
        onTopicClick = { navigateToUri(it) },
        onUserClick = navigator::navigateToUserProfile
    )
    topicEntry(
        onBackClick = navigator::goBack,
        onWebViewClick = navigator::navigateToWebView,
        onGroupClick = navigator::navigateToGroup,
        onReshareStatusesClick = navigator::navigateToReshareStatuses,
        onUserClick = navigator::navigateToUserProfile,
        onImageClick = navigator::navigateToImageViewer,
        onOpenDeepLinkUrl = navigateToUri
    )
    reshareStatusesEntry(
        onBackClick = navigator::goBack,
        onUserClick = navigator::navigateToUserProfile
    )
    webViewEntry(onBackClick = navigator::goBack)
    loginEntry(
        onPopBackStack = navigator::goBack,
        onOpenDeepLinkUrl = navigateToUri
    )
    verifyPhoneEntry(
        onBackClick = navigator::goBack,
        onSuccess = { navigator.popUpTo(LoginNavKey::class, true) }
    )
    settingsEntry(
        onBackClick = navigator::goBack,
        onGroupDefaultNotificationsPreferencesSettingsClick =
            navigator::navigateToGroupDefaultNotificationsPreferencesSettings,
        onLoginClick = navigator::navigateToLogin
    )
    groupDefaultNotificationsPreferencesSettingsEntry(
        onBackClick = navigator::goBack
    )
    imageViewerEntry(
        navigateUp = navigator::goBack
    )
    interestsEntry(
        onBackClick = navigator::goBack,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook
    )
    searchSubjectsEntry(
        onBackClick = navigator::goBack,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook,
    )
    movieEntry(
        onBackClick = navigator::goBack,
        onLoginClick = navigator::navigateToLogin,
        onImageClick = navigator::navigateToImageViewer,
        onUserClick = navigator::navigateToUserProfile,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook
    )
    tvEntry(
        onBackClick = navigator::goBack,
        onLoginClick = navigator::navigateToLogin,
        onImageClick = navigator::navigateToImageViewer,
        onUserClick = navigator::navigateToUserProfile,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook
    )
    bookEntry(
        onBackClick = navigator::goBack,
        onLoginClick = navigator::navigateToLogin,
        onImageClick = navigator::navigateToImageViewer,
        onUserClick = navigator::navigateToUserProfile,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook
    )
    rankListEntry(
        onBackClick = navigator::goBack,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onBookClick = navigator::navigateToBook
    )
    createdDouListsEntry(
        onBackClick = navigator::goBack,
        onDouListClick = navigator::navigateToDouList
    )
    douListEntry(
        onBackClick = navigator::goBack,
        onTopicClick = { navigateToUri(it) },
        onBookClick = navigator::navigateToBook,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onUserClick = navigator::navigateToUserProfile,
        onImageClick = navigator::navigateToImageViewer
    )
    myDouListsEntry(
        onBackClick = navigator::goBack,
        onTopicClick = { navigateToUri(it) },
        onBookClick = navigator::navigateToBook,
        onMovieClick = navigator::navigateToMovie,
        onTvClick = navigator::navigateToTv,
        onUserClick = navigator::navigateToUserProfile,
        onImageClick = navigator::navigateToImageViewer,
        onDouListClick = navigator::navigateToDouList
    )
}