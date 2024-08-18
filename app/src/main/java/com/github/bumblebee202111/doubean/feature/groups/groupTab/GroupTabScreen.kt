package com.github.bumblebee202111.doubean.feature.groups.groupTab

import android.content.res.ColorStateList
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.DialogContentGroupTabNotificationsPreferenceBinding
import com.github.bumblebee202111.doubean.databinding.ListItemPostBinding
import com.github.bumblebee202111.doubean.databinding.ViewGroupTabActionsBinding
import com.github.bumblebee202111.doubean.feature.groups.common.NotificationsButton
import com.github.bumblebee202111.doubean.feature.groups.common.TopicCountLimitEachFetchTextField
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage
import com.github.bumblebee202111.doubean.ui.common.rememberLazyListStatePagingWorkaround
import com.github.bumblebee202111.doubean.ui.component.DateTimeText
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.abbreviatedDateTimeString
import com.github.bumblebee202111.doubean.util.getColorFromTheme
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar

@Composable
fun GroupTabScreen(
    groupTabViewModel: GroupTabViewModel,
    group: GroupDetail?,
    navigateToTopic: (topicId: String) -> Unit,
) {

    val topicPagingItems = groupTabViewModel.topicsPagingData.collectAsLazyPagingItems()
    val tabId = groupTabViewModel.tabId
    val context = LocalContext.current
    var openAlertDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection()),
        state = topicPagingItems.rememberLazyListStatePagingWorkaround()

    ) {

        item(
            key = "tab_actions", contentType = "tab_actions"
        ) {

            AndroidViewBinding(
                factory = { inflater, parent, attachToParent ->
                    ViewGroupTabActionsBinding.inflate(
                        inflater,
                        parent,
                        attachToParent
                    ).apply {
                        fun setupSpinner() {
                            val arrayAdapter = ArrayAdapter.createFromResource(
                                context,
                                R.array.sort_topics_by_array,
                                android.R.layout.simple_spinner_item
                            )
                            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                            sortTopicsBySpinner.adapter = arrayAdapter
                            sortTopicsBySpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        
                                        view: View?,
                                        position: Int,
                                        id: Long,
                                    ) {
                                        groupTabViewModel.setSortBy(getSortByAt(position))
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                    }
                                }
                        }
                        setupSpinner()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                onReset = {}
            ) {

                fun setupFavoriteButtonAndMore() {

                    if (tabId == null) { 
                        notificationsButton.visibility = View.GONE
                        favoriteButton.visibility = View.GONE
                        more.visibility = View.GONE
                    } else { 
                        favoriteButton.setOnClickListener {
                            group?.findTab(tabId)?.let { tab ->
                                if (tab.isFavorite) {
                                    groupTabViewModel.removeFavorite()
                                    root.showSnackbar(
                                        R.string.unfavorited_tab,
                                        Snackbar.LENGTH_LONG
                                    )
                                } else {
                                    groupTabViewModel.addFavorite()
                                    root.showSnackbar(
                                        R.string.favorited_tab,
                                        Snackbar.LENGTH_LONG,
                                        R.string.edit_follow_preferences
                                    ) { openAlertDialog = true }
                                }
                            }
                        }

                        @Suppress("ConstantConditionIf") 
                        if (false) {
                            notificationsButton.setContent {
                                group?.let { group ->
                                    group.findTab(tabId)?.let { tab ->
                                        NotificationsButton(
                                            groupColor = group.color?.let {
                                                Color(it)
                                            } ?: LocalContentColor.current,
                                            enableNotifications = tab.enableNotifications ?: false
                                        ) {
                                            openAlertDialog = true
                                        }
                                    }
                                }
                            }
                        }

                        more.setOnClickListener { v ->
                            val popup = PopupMenu(context, v)
                            popup.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.action_share -> {
                                        val shareText = StringBuilder()
                                        group?.let { group ->
                                            shareText.append(group.name + "|")
                                            group.tabs?.first { it.id == tabId }
                                                ?.let { tab ->
                                                    shareText.append(tab.name)
                                                }
                                            group.shareUrl?.let { shareUrl ->
                                                shareText.append(" $shareUrl\r\n")
                                            }

                                            ShareUtil.share(context, shareText)
                                            true
                                        }

                                    }
                                }
                                false
                            }

                            popup.inflate(R.menu.menu_group_tab)
                            popup.show()
                        }
                    }
                }

                setupFavoriteButtonAndMore()

                group?.let { group ->
                    val colorSurface = context.getColorFromTheme(R.attr.colorSurface)
                    val groupColor =
                        group.color ?: context.getColorFromTheme(R.attr.colorPrimary)

                    group.findTab(tabId)?.let { tab ->
                        with(favoriteButton) {
                            if (tab.isFavorite) {
                                setIconResource(R.drawable.ic_star)
                                setText(R.string.favorited)
                                iconTint = ColorStateList.valueOf(groupColor)
                                setTextColor(groupColor)
                                setBackgroundColor(colorSurface)
                            } else {
                                setIconResource(R.drawable.ic_star)
                                setText(R.string.favorite)
                                iconTint = ColorStateList.valueOf(colorSurface)
                                setTextColor(colorSurface)
                                setBackgroundColor(groupColor)
                            }
                        }

                    }
                }
            }
        }

        items(
            count = topicPagingItems.itemCount,
            key = topicPagingItems.itemKey { it.id },
            contentType = topicPagingItems.itemContentType { "topicLazyPagingItem" }
        ) { index ->
            val topic = topicPagingItems[index]
            AndroidViewBinding(factory = ListItemPostBinding::inflate,
                modifier = Modifier.fillMaxWidth(),
                onReset = {}
            ) {
                post = topic

                cover.setContent {
                    AsyncImage(
                        model = topic?.coverUrl, contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_size_normal))),
                        contentScale = ContentScale.Crop
                    )
                }

                clickListener =
                    View.OnClickListener { topic?.let { navigateToTopic(it.id) } }
                showPopup = View.OnClickListener { v ->
                    val popupMenu = PopupMenu(v.context, more)
                    popupMenu.inflate(R.menu.menu_post_item)
                    popupMenu.setOnMenuItemClickListener { item ->
                        topic?.let {
                            when (item.itemId) {
                                R.id.action_share -> {
                                    val shareText = StringBuilder()
                                    group?.let { group ->
                                        shareText.append(group.name + "|")
                                    }

                                    it.tag?.let { tag ->
                                        shareText.append(tag.name)
                                    }
                                    shareText.append("@${it.author.name}${context.getString(R.string.colon)} ${it.title} ${it.url}")
                                    ShareUtil.share(context, shareText)
                                    false
                                }

                                else -> false
                            }
                        }
                        false
                    }
                    popupMenu.show()
                }

                authorAvatar.setContent {
                    UserProfileImage(
                        url = topic?.author?.avatarUrl,
                        size = dimensionResource(id = R.dimen.icon_size_extra_small)
                    )
                }

                created.setContent {
                    topic?.created?.let {
                        DateTimeText(text = it.abbreviatedDateTimeString(LocalContext.current))
                    }
                }

                commentIcon.setContent {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Comment,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_small))
                    )
                }

                lastUpdated.setContent {
                    topic?.lastUpdated?.let {
                        DateTimeText(text = it.abbreviatedDateTimeString(LocalContext.current))
                    }
                }

            }
            if (index != topicPagingItems.itemCount - 1) {
                HorizontalDivider()
            }

        }
    }

    if (openAlertDialog) {
        group?.tabs?.find { it.id == tabId }?.let { tab ->
            val enableNotifications = tab.enableNotifications
            val allowNotificationUpdates = tab.allowDuplicateNotifications
            val sortRecommendedTopicsBy = tab.sortRecommendedTopicsBy
            val numberOfTopicsLimitEachFeedFetch = tab.feedRequestTopicCountLimit
            if (enableNotifications != null && allowNotificationUpdates != null && sortRecommendedTopicsBy != null && numberOfTopicsLimitEachFeedFetch != null) {

                GroupTabNotificationsPreferenceDialog(
                    initialEnableNotifications = enableNotifications,
                    initialAllowNotificationUpdates = allowNotificationUpdates,
                    initialSortRecommendedTopicsBy = sortRecommendedTopicsBy,
                    initialNumberOfTopicsLimitEachFeedFetch = numberOfTopicsLimitEachFeedFetch,
                    onDismissRequest = {
                        openAlertDialog = false
                    }) { enableNotificationsToSave, allowNotificationUpdatesToSave, sortRecommendedTopicsByToSave, numberOfTopicsLimitEachFeedFetchToSave ->
                    groupTabViewModel.saveNotificationsPreference(
                        enableNotifications = enableNotificationsToSave,
                        allowNotificationUpdates = allowNotificationUpdatesToSave,
                        sortRecommendedTopicsBy = sortRecommendedTopicsByToSave,
                        numberOfTopicsLimitEachFeedFetch = numberOfTopicsLimitEachFeedFetchToSave
                    )
                    openAlertDialog = false
                }
            }

        }
    }
}

@Composable
fun GroupTabNotificationsPreferenceDialog(
    initialEnableNotifications: Boolean,
    initialAllowNotificationUpdates: Boolean,
    initialSortRecommendedTopicsBy: TopicSortBy,
    initialNumberOfTopicsLimitEachFeedFetch: Int,
    onDismissRequest: () -> Unit,
    onConfirmation: (enableNotifications: Boolean, allowNotificationUpdates: Boolean, sortRecommendedTopicsBy: TopicSortBy, numberOfTopicsLimitEachFeedFetch: Int) -> Unit,
) {

    var enableNotifications by remember {
        mutableStateOf(initialEnableNotifications)
    }

    var allowNotificationUpdates by remember {
        mutableStateOf(initialAllowNotificationUpdates)
    }
    var sortRecommendedTopicsBy by remember {
        mutableStateOf(initialSortRecommendedTopicsBy)
    }
    var numberOfTopicsLimitEachFeedFetch by remember {
        mutableIntStateOf(initialNumberOfTopicsLimitEachFeedFetch)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirmation(
                    enableNotifications,
                    allowNotificationUpdates,
                    sortRecommendedTopicsBy,
                    numberOfTopicsLimitEachFeedFetch
                )
            }) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(text = stringResource(id = R.string.group_notifications_preference)) },
        text = {
            AndroidViewBinding(factory = { inflater, root, attachToRoot ->
                DialogContentGroupTabNotificationsPreferenceBinding.inflate(
                    inflater,
                    root,
                    attachToRoot
                ).apply {
                    sortRecommendedTopicsBySpinner.adapter = ArrayAdapter.createFromResource(
                        root.context,
                        R.array.sort_recommended_topics_by_array,
                        android.R.layout.simple_spinner_item
                    )
                        .apply { setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item) }
                }
            }) {

                fun getTopicSortByAt(spinnerItemPosition: Int) =
                    when (spinnerItemPosition) {
                        0 -> TopicSortBy.LAST_UPDATED
                        1 -> TopicSortBy.NEW_TOP
                        else -> throw java.lang.IndexOutOfBoundsException()
                    }

                fun getSpinnerItemPositionOf(topicSortBy: TopicSortBy) =
                    when (topicSortBy) {
                        TopicSortBy.LAST_UPDATED -> 0
                        TopicSortBy.NEW_TOP -> 1
                        else -> throw java.lang.IndexOutOfBoundsException()
                    }

                enableGroupNotificationsPref.apply {
                    isChecked = enableNotifications
                    setOnCheckedChangeListener { _, isChecked ->
                        enableNotifications = isChecked
                    }
                }
                allowDuplicateNotificationsPref.apply {
                    isEnabled = enableNotifications
                    isChecked = allowNotificationUpdates
                    setOnCheckedChangeListener { _, isChecked ->
                        allowNotificationUpdates = isChecked
                    }
                }
                sortRecommendedTopicsByTitle.isEnabled = enableNotifications
                sortRecommendedTopicsBySpinner.apply {
                    isEnabled = enableNotifications
                    setSelection(getSpinnerItemPositionOf(sortRecommendedTopicsBy))
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long,
                        ) {
                            sortRecommendedTopicsBy = getTopicSortByAt(position)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                    }
                }
                feedRequestTopicCountLimitTitle.isEnabled = enableNotifications
                feedRequestTopicCountLimitTextField.setContent {
                    TopicCountLimitEachFetchTextField(
                        numberOfTopicsLimitEachFeedFetch = numberOfTopicsLimitEachFeedFetch,
                        onUpdateNumberOfTopicsLimitEachFeedFetch = {
                            numberOfTopicsLimitEachFeedFetch = it
                        },
                        enabled = enableNotifications
                    )
                }

            }
        }
    )
}

private fun getSortByAt(position: Int): TopicSortBy {
    return when (position) {
        0 -> TopicSortBy.LAST_UPDATED
        1 -> TopicSortBy.NEW
        2 -> TopicSortBy.TOP
        else -> throw (IndexOutOfBoundsException())
    }
}

