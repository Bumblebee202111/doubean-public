package com.github.bumblebee202111.doubean.ui.common.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectRatingBar
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectRatingBarSize
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectRoundedCornerShape
import com.github.bumblebee202111.doubean.feature.subjects.common.SubjectTypeLabel
import com.github.bumblebee202111.doubean.model.subjects.Book
import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@Composable
fun SubjectItem(
    basicContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    rankContent: (@Composable () -> Unit)? = null,
    interestButton: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                onClick?.let {
                    Modifier.clickable(onClick = onClick)
                } ?: Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (rankContent != null) {
            rankContent()
            Spacer(modifier = Modifier.size(8.dp))
        }
        basicContent()
        if (interestButton != null) {
            Spacer(modifier = Modifier.size(4.dp))
            interestButton()
        }
    }
}

@Composable
fun RowScope.SubjectItemBasicContent(subject: Subject, showType: Boolean = false) {
    SubjectItemImage(url = subject.imageUrl)
    Spacer(modifier = Modifier.size(8.dp))
    SubjectItemColumn(subject = subject, showType = showType)
}


@Composable
fun RowScope.SubjectItemColumn(
    subject: Subject,
    showType: Boolean = false,
) {
    with(subject) {
        SubjectItemColumn(
            title = title,
            rating = rating,
            cardSubtitle = cardSubtitle,
            type = subject.type,
            subtitle = (subject as? Book)?.subtitle,
            showType = showType
        )
    }
}

@Composable
internal fun RowScope.SubjectItemColumn(
    title: String,
    rating: Rating,
    cardSubtitle: String,
    type: SubjectType,
    subtitle: String? = null,
    showType: Boolean = false,
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        SubjectRatingBar(rating = rating, size = SubjectRatingBarSize.Compact)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showType) {
                SubjectTypeLabel(type = type)
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = cardSubtitle,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

    }

}

@Composable
fun SubjectItemImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .width(50.dp)
            .clip(SubjectRoundedCornerShape),
        contentScale = ContentScale.FillWidth
    )
}


@Composable
fun SubjectItemRank(rankValue: Int, listSize: Int) {
    val rankTextLength = listSize.toString().length
    val rankText = rankValue.toString().padStart(rankTextLength)
    Text(
        text = rankText,
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = FontFamily.Monospace
    )
}