package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subject.Rating
import com.github.bumblebee202111.doubean.model.subject.SubjectDetail
import com.github.bumblebee202111.doubean.model.subject.SubjectInterestStatus
import com.github.bumblebee202111.doubean.ui.component.DoubeanRatingBar
import com.github.bumblebee202111.doubean.ui.component.DoubeanRatingBarSize
import com.github.bumblebee202111.doubean.ui.component.InlineLoginPrompt

@Composable
fun SubjectDetailHeader(
    subject: SubjectDetail,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus, rating: Int?) -> Unit,
    onImageClick: (url: String) -> Unit,
    onMetaInfoClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = subject.coverUrl,
            contentDescription = null,
            modifier = Modifier
                .size(width = 100.dp, height = 140.dp)
                .clip(SubjectRoundedCornerShape)
                .clickable { onImageClick(subject.coverUrl) },
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .weight(1F),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column {
                TitleText(subject = subject)
                SubtitleText(subject = subject)
            }

            SubjectRatingBar(rating = subject.rating, size = SubjectRatingBarSize.Expanded)

            MetaInfoText(subject = subject, onClick = onMetaInfoClick)
            VendorsSection(subject = subject)

            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(4.dp))

                MyPersonalRating(subject = subject)
                SubjectInterestButtons(
                    subject = subject,
                    onUpdateStatus = onUpdateStatus
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                InlineLoginPrompt(
                    promptText = stringResource(R.string.login_prompt_mark_subject),
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}

@Composable
private fun TitleText(subject: SubjectDetail) {
    Text(
        text = subject.displayTitle.getString(),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SubtitleText(subject: SubjectDetail) {
    subject.displaySubtitle?.let {
        Text(
            text = it.getString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MyPersonalRating(subject: SubjectDetail) {
    val myRating = subject.interest?.rating
    if (myRating is Rating.NonNull && myRating.value > 0) {
        val normalizedRating = (myRating.value / myRating.max * 5).coerceIn(0f, 5f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.my_rating_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            DoubeanRatingBar(
                rating = normalizedRating,
                size = DoubeanRatingBarSize.Small
            )
        }
    }
}

private const val META_INFO_ARROW_ID = "meta_info_arrow"

@Composable
private fun MetaInfoText(
    subject: SubjectDetail,
    onClick: () -> Unit,
) {
    val metaInfo = subject.displayMetaInfo
    if (metaInfo.isBlank()) return

    val annotatedText = remember(metaInfo) {
        buildAnnotatedString {
            append(metaInfo)
            append(" ")
            appendInlineContent(META_INFO_ARROW_ID, "[>]")
        }
    }
    val inlineContent = remember {
        mapOf(
            META_INFO_ARROW_ID to InlineTextContent(
                Placeholder(
                    width = 14.sp,
                    height = 14.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        )
    }

    Text(
        text = annotatedText,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp)
    )
}

@Composable
private fun VendorsSection(
    subject: SubjectDetail,
    modifier: Modifier = Modifier,
) {
    if (subject.vendors.isEmpty()) return

    val titleResId = subject.type.vendorTitleResId ?: return
    val title = stringResource(titleResId)

    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subject.vendors.take(3).forEach { vendor ->
                AsyncImage(
                    model = vendor.icon,
                    contentDescription = vendor.title,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            try {
                                uriHandler.openUri(vendor.uri)
                            } catch (e: Exception) {
                                try {
                                    uriHandler.openUri(vendor.url)
                                } catch (e: Exception) {
                                    
                                }
                            }
                        }
                )
            }
        }
    }
}