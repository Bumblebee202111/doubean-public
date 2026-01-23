package com.github.bumblebee202111.doubean.feature.groups.topic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.groups.shared.groupTopAppBarColor
import com.github.bumblebee202111.doubean.model.groups.TopicDetail
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.MoreButton
import com.github.bumblebee202111.doubean.util.toColorOrPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicTopAppBar(
    topic: TopicDetail?,
    canJump: Boolean,
    onBackClick: () -> Unit,
    onJumpToCommentClick: () -> Unit,
    onWebViewClick: (String) -> Unit,
    onOpenInDoubanClick: (String) -> Unit,
) {
    var appBarMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var viewInMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val groupColor = topic?.group?.color.toColorOrPrimary()

    DoubeanTopAppBar(
        title = {
            topic?.title?.let {
                Text(
                    text = it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        actions = {
            MoreButton(onClick = { appBarMenuExpanded = !appBarMenuExpanded })

            DropdownMenu(
                expanded = appBarMenuExpanded,
                onDismissRequest = { appBarMenuExpanded = false }) {
                if (topic != null && canJump) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.jump_to_comment)) },
                        onClick = {
                            appBarMenuExpanded = false
                            onJumpToCommentClick()
                        }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.view_in))
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        appBarMenuExpanded = false
                        viewInMenuExpanded = true
                    })
            }

            DropdownMenu(
                expanded = viewInMenuExpanded,
                onDismissRequest = { viewInMenuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.view_in_web)) },
                    onClick = {
                        viewInMenuExpanded = false
                        topic?.url?.let(onWebViewClick)
                    })
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.view_in_douban)) },
                    onClick = {
                        topic?.uri?.let {
                            viewInMenuExpanded = false
                            onOpenInDoubanClick(it)
                        }
                    })

            }
        },
        colors = groupTopAppBarColor(groupColor)
    )
}
