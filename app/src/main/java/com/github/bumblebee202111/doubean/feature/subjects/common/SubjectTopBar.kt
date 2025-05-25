package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.SubjectDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.util.OpenInUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectTopBar(
    subjectType: SubjectType,
    subject: SubjectDetail?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    DoubeanTopAppBar(
        title = {
            Column {
                if (subject != null) {
                    Text(
                        text = subject.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                SubjectTypeLabel(type = subjectType)
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            var expanded by remember {
                mutableStateOf(false)
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                subject?.let {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.view_in_douban)) },
                        onClick = {
                            OpenInUtils.openInDouban(context, subject.uri)
                            expanded = false
                        })
                }
            }
        }
    )
}
