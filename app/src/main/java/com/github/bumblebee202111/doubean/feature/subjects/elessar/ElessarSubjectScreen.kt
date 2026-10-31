package com.github.bumblebee202111.doubean.feature.subjects.elessar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.common.SimpleSubjectItemContent
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectRoundedCornerShape
import com.github.bumblebee202111.doubean.model.Photo
import com.github.bumblebee202111.doubean.model.subject.ElessarBaseSubject
import com.github.bumblebee202111.doubean.model.subject.ElessarModule
import com.github.bumblebee202111.doubean.model.subject.ElessarSubject
import com.github.bumblebee202111.doubean.model.subject.ElessarWork
import com.github.bumblebee202111.doubean.model.subject.ElessarWorkCollection
import com.github.bumblebee202111.doubean.model.subject.Rating
import com.github.bumblebee202111.doubean.model.subject.SubjectType
import com.github.bumblebee202111.doubean.ui.component.BackButton
import com.github.bumblebee202111.doubean.ui.component.DoubeanTopAppBar
import com.github.bumblebee202111.doubean.ui.component.ExpandCollapseText
import com.github.bumblebee202111.doubean.ui.component.FullScreenErrorWithRetry
import com.github.bumblebee202111.doubean.ui.component.FullScreenLoadingIndicator
import com.github.bumblebee202111.doubean.ui.component.MoreButton
import com.github.bumblebee202111.doubean.util.OpenInUtils
import com.github.bumblebee202111.doubean.util.ShareUtil

private const val CollapsedTextMaxLines = 4


@Composable
fun ElessarSubjectScreen(
    onBackClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (uri: String) -> Boolean,
    viewModel: ElessarSubjectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ElessarSubjectScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onImageClick = onImageClick,
        onOpenDeepLinkUrl = onOpenDeepLinkUrl,
        onRetryClick = viewModel::fetchData,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElessarSubjectScreen(
    uiState: ElessarSubjectUiState,
    onBackClick: () -> Unit,
    onImageClick: (url: String) -> Unit,
    onOpenDeepLinkUrl: (uri: String) -> Boolean,
    onRetryClick: () -> Unit,
) {
    val elessarSubject = (uiState as? ElessarSubjectUiState.Success)?.elessarSubject

    Scaffold(
        topBar = {
            ElessarSubjectTopBar(
                elessarSubject = elessarSubject,
                onBackClick = onBackClick,
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is ElessarSubjectUiState.Loading -> {
                FullScreenLoadingIndicator(contentPadding = innerPadding)
            }

            is ElessarSubjectUiState.Error -> {
                FullScreenErrorWithRetry(
                    message = uiState.message.getString(),
                    onRetryClick = onRetryClick,
                    contentPadding = innerPadding,
                )
            }

            is ElessarSubjectUiState.Success -> {
                LazyColumn(contentPadding = innerPadding) {
                    item(key = "header") {
                        ElessarSubjectHeaderView(
                            elessarSubject = uiState.elessarSubject,
                            onImageClick = onImageClick,
                        )
                    }

                    if (uiState.elessarSubject.desc.isNotBlank()) {
                        item(key = "intro") {
                            ElessarIntroView(desc = uiState.elessarSubject.desc)
                        }
                    }

                    uiState.elessarSubject.modules.forEach { module ->
                        when (module) {
                            is ElessarModule.WorkCollections -> {
                                items(
                                    items = module.collections,
                                    key = { "work_collection_${module.id}_${it.title}" }
                                ) { collection ->
                                    WorksHorizontalView(
                                        collection = collection,
                                        onWorkClick = { onOpenDeepLinkUrl(it) },
                                    )
                                }
                            }

                            is ElessarModule.Photos -> {
                                item(key = "photos_${module.id}_${module.title}") {
                                    ElessarPhotosView(
                                        title = module.title,
                                        total = module.total,
                                        photos = module.photos,
                                        onImageClick = onImageClick,
                                    )
                                }
                            }

                            is ElessarModule.SubjectCollection -> {
                                item(key = "subjects_${module.id}_${module.title}") {
                                    SubjectsHorizontalView(
                                        title = module.title,
                                        total = module.total,
                                        subjects = module.subjects,
                                        onSubjectClick = { onOpenDeepLinkUrl(it) },
                                    )
                                }
                            }

                            is ElessarModule.AwardResultCollection -> Unit
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ElessarSubjectTopBar(
    elessarSubject: ElessarSubject?,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    DoubeanTopAppBar(
        titleText = stringResource(R.string.title_personage),
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        actions = {
            if (elessarSubject != null) {
                val shareUrl = elessarSubject.sharingUrl.ifEmpty { elessarSubject.url }
                if (shareUrl.isNotBlank()) {
                    IconButton(onClick = { ShareUtil.share(context, shareUrl) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                        )
                    }
                }
                MoreButton(onClick = { showMenu = true })
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (elessarSubject.uri.isNotBlank()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_douban)) },
                            onClick = {
                                showMenu = false
                                OpenInUtils.openInDouban(context, elessarSubject.uri)
                            }
                        )
                    }
                    if (elessarSubject.url.isNotBlank()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.view_in_browser)) },
                            onClick = {
                                showMenu = false
                                OpenInUtils.openInBrowser(context, elessarSubject.url)
                            }
                        )
                    }
                }
            }
        }
    )
}


@Composable
private fun ElessarSubjectHeaderView(
    elessarSubject: ElessarSubject,
    onImageClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayAvatarUrl = elessarSubject.cover?.normal?.url ?: elessarSubject.coverUrl
    val fullAvatarUrl = elessarSubject.cover?.large?.url ?: elessarSubject.coverUrl

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model = displayAvatarUrl,
            contentDescription = elessarSubject.title,
            modifier = Modifier
                .width(80.dp)
                .height(112.dp)
                .clip(SubjectRoundedCornerShape)
                .then(
                    if (fullAvatarUrl.isNotBlank()) {
                        Modifier.clickable { onImageClick(fullAvatarUrl) }
                    } else {
                        Modifier
                    }
                ),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = elessarSubject.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (elessarSubject.latinTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = elessarSubject.latinTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val extra = elessarSubject.extra
            val infoText = remember(extra) {
                when {
                    extra == null -> ""
                    extra.info.isNotEmpty() -> extra.info.joinToString("\n") { (k, v) -> "$k: $v" }
                    !extra.shortInfo.isNullOrBlank() -> extra.shortInfo
                    else -> ""
                }
            }
            if (infoText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ExpandCollapseText(
                    text = infoText,
                    maxLines = CollapsedTextMaxLines,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
            }
        }
    }
}


@Composable
private fun ElessarIntroView(
    desc: String,
    modifier: Modifier = Modifier,
) {
    ElessarModuleSection(
        title = stringResource(R.string.subject_module_title_intro),
        modifier = modifier,
    ) {
        ExpandCollapseText(
            text = desc,
            maxLines = CollapsedTextMaxLines,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


@Composable
private fun WorksHorizontalView(
    collection: ElessarWorkCollection,
    onWorkClick: (uri: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElessarModuleSection(
        title = collection.title,
        total = collection.total.takeIf { it > 0 },
        modifier = modifier,
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = collection.works,
                key = { "${it.subject.id}_${it.roles.joinToString()}" }
            ) { work ->
                ElessarWorkItem(
                    work = work,
                    onClick = { onWorkClick(work.subject.uri) },
                )
            }
        }
    }
}

@Composable
private fun ElessarWorkItem(
    work: ElessarWork,
    onClick: () -> Unit,
) {
    val subject = work.subject
    val imageUrl = subject.cover?.normal?.url ?: subject.coverUrl
    val rating = subject.extra?.rating ?: Rating.Null("")
    val subtitle = remember(work) {
        listOfNotNull(
            subject.extra?.year?.takeIf { it.isNotBlank() },
            work.roles.firstOrNull()?.takeIf { it.isNotBlank() }
        ).joinToString(" ").ifEmpty { null }
    }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
    ) {
        SimpleSubjectItemContent(
            imageUrl = imageUrl,
            title = subject.title,
            rating = rating,
            subtitle = subtitle,
            isSquare = subject.subjectType == SubjectType.MUSIC,
        )
    }
}


@Composable
private fun ElessarPhotosView(
    title: String,
    total: Int,
    photos: List<Photo>,
    onImageClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElessarModuleSection(
        title = title,
        total = total.takeIf { it > 0 },
        modifier = modifier,
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(items = photos, key = { it.id ?: it.image.normal.url }) { photo ->
                AsyncImage(
                    model = photo.image.normal.url,
                    contentDescription = null,
                    modifier = Modifier
                        .height(160.dp)
                        .clickable { onImageClick(photo.image.large.url) },
                    contentScale = ContentScale.FillHeight,
                )
            }
        }
    }
}


@Composable
private fun SubjectsHorizontalView(
    title: String,
    total: Int,
    subjects: List<ElessarBaseSubject>,
    onSubjectClick: (uri: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElessarModuleSection(
        title = title,
        total = total.takeIf { it > 0 },
        modifier = modifier,
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = subjects, key = { it.id }) { person ->
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onSubjectClick(person.uri) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AsyncImage(
                        model = person.cover?.normal?.url ?: person.coverUrl,
                        contentDescription = person.title,
                        modifier = Modifier
                            .width(80.dp)
                            .height(112.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = person.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ElessarModuleSection(
    title: String,
    modifier: Modifier = Modifier,
    total: Int? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            total?.let {
                Text(
                    text = it.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        content()
    }
}