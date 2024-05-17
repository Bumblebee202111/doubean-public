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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.databinding.ListItemPostBinding
import com.github.bumblebee202111.doubean.databinding.ViewGroupTabActionsBinding
import com.github.bumblebee202111.doubean.model.GroupDetail
import com.github.bumblebee202111.doubean.model.TopicSortBy
import com.github.bumblebee202111.doubean.ui.common.UserProfileImage
import com.github.bumblebee202111.doubean.ui.common.rememberLazyListStatePagingWorkaround
import com.github.bumblebee202111.doubean.util.ShareUtil
import com.github.bumblebee202111.doubean.util.getColorFromTheme
import com.github.bumblebee202111.doubean.util.showSnackbar
import com.google.android.material.snackbar.Snackbar

@Composable
fun GroupTabScreen(
    groupTabViewModel: GroupTabViewModel,
    group: GroupDetail?,
    navigateToTopic: (topicId: String) -> Unit,
    showTabNotificationsPreferenceDialog: (tabId: String) -> Unit,
) {

    val topicPagingItems = groupTabViewModel.topicsPagingData.collectAsLazyPagingItems()
    val tabId = groupTabViewModel.tabId
    val context = LocalContext.current

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
                                R.array.sort_posts_by_array,
                                android.R.layout.simple_spinner_item
                            )
                            arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                            sortPostsBySpinner.adapter = arrayAdapter
                            sortPostsBySpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        // A Kotlin bug: https://stackoverflow.com/questions/48343622/how-to-fix-parameter-specified-as-non-null-is-null-on-rotating-screen-in-a-fragm
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

                    if (tabId == null) { //All
                        toggleGroup.visibility = View.GONE
                        more.visibility = View.GONE
                    } else { //Non-all tab
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
                                        R.string.edit_favorite_preferences
                                    ) { showTabNotificationsPreferenceDialog(tabId) }
                                }
                            }
                        }

                        notificationButton.setOnClickListener {
                            showTabNotificationsPreferenceDialog(tabId)
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
                                            shareText.append(' ' + group.shareUrl + "\r\n")
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
                    notificationButton.visibility =
                        if (group.findTab(tabId)?.enableNotifications == null) View.GONE else View.VISIBLE
                    group.findTab(tabId)?.let { tab ->
                        with(favoriteButton) {
                            if (tab.isFavorite) {
                                setIconResource(R.drawable.ic_remove)
                                setText(R.string.unfavorite)
                                iconTint = ColorStateList.valueOf(groupColor)
                                setTextColor(groupColor)
                                setBackgroundColor(colorSurface)
                            } else {
                                setIconResource(R.drawable.ic_add)
                                setText(R.string.favorite)
                                iconTint = ColorStateList.valueOf(colorSurface)
                                setTextColor(colorSurface)
                                setBackgroundColor(groupColor)
                            }
                        }

                        with(notificationButton) {
                            when (tab.enableNotifications) {
                                true -> {
                                    setIconResource(R.drawable.ic_notifications)
                                    iconTint = ColorStateList.valueOf(groupColor)
                                    setBackgroundColor(colorSurface)
                                }

                                false -> {
                                    setIconResource(R.drawable.ic_notification_add)
                                    iconTint = ColorStateList.valueOf(colorSurface)
                                    setBackgroundColor(groupColor)
                                }

                                else -> {

                                }
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
                executePendingBindings()

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
                        post?.let {
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

            }
            if (index != topicPagingItems.itemCount - 1) {
                HorizontalDivider()
            }

        }
    }
}

private fun getSortByAt(position: Int): TopicSortBy {
    return when (position) {
        0 -> TopicSortBy.LAST_UPDATED
        1 -> TopicSortBy.NEW
        2 -> TopicSortBy.TOP
        else -> throw (IndexOutOfBoundsException())
    }
}

