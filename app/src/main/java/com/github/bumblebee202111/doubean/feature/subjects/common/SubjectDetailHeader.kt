package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.BookDetail
import com.github.bumblebee202111.doubean.model.subjects.MovieDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectDetail
import com.github.bumblebee202111.doubean.model.subjects.SubjectInterestStatus
import com.github.bumblebee202111.doubean.model.subjects.TvDetail
import com.github.bumblebee202111.doubean.ui.component.LoginPromptText

@Composable
fun SubjectDetailHeader(
    subject: SubjectDetail,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onUpdateStatus: (newStatus: SubjectInterestStatus) -> Unit,
    onImageClick: (url: String) -> Unit,
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
            MetaInfoText(subject = subject)
            if (isLoggedIn) {
                SubjectInterestButtons(
                    subject = subject,
                    onUpdateStatus = onUpdateStatus
                )
            } else {
                LoginPromptText(
                    contentStringRes = R.string.login_prompt_to_mark_subjects,
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}

@Composable
private fun TitleText(subject: SubjectDetail) {
    val titleText = when (subject) {
        is MovieDetail -> {
            subject.originalTitle?.let {
                subject.title
            } ?: stringResource(
                id = R.string.subject_title_year,
                subject.title,
                subject.year
            )
        }

        is TvDetail -> {
            subject.originalTitle?.let {
                subject.title
            } ?: stringResource(
                id = R.string.subject_title_year,
                subject.title,
                subject.year
            )
        }

        is BookDetail -> subject.title
    }
    Text(
        text = titleText,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SubtitleText(subject: SubjectDetail) {
    val subtitleText = when (subject) {
        is MovieDetail -> {
            subject.originalTitle?.let {
                stringResource(
                    id = R.string.subject_title_year,
                    it,
                    subject.year
                )
            }
        }

        is TvDetail -> {
            subject.originalTitle?.let {
                stringResource(
                    id = R.string.subject_title_year,
                    it,
                    subject.year
                )
            }
        }

        is BookDetail -> {
            subject.subtitle
        }
    }
    subtitleText?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
private fun MetaInfoText(subject: SubjectDetail) {
    val metaInfo = when (subject) {
        is MovieDetail -> with(subject) {
            buildList {
                countries.toMetaInfoSegment()?.let {
                    add(it)
                }
                genres.toMetaInfoSegment(n = 3, separator = " ")?.let {
                    add(it)
                }
                pubdate.toMetaInfoSegment(postfix = "上映")?.let {
                    add(it)
                }
                durations.toMetaInfoSegment(n = 2, prefix = "片长")?.let {
                    add(it)
                }
            }.joinToString(" / ")
        }

        is TvDetail -> with(subject) {
            buildList {
                countries.toMetaInfoSegment()?.let {
                    add(it)
                }
                genres.toMetaInfoSegment(n = 3, separator = " ")?.let {
                    add(it)
                }
                pubdate.toMetaInfoSegment(postfix = "首播")?.let {
                    add(it)
                }
                add("共${episodesCount}集")
                durations.toMetaInfoSegment(n = 2, prefix = "单集片长")?.let {
                    add(it)
                }
            }.joinToString(" / ")
        }

        is BookDetail -> with(subject) {
            buildList {
                author.toMetaInfoSegment()?.let {
                    add(it)
                }
                press.toMetaInfoSegment()?.let {
                    add(it)
                }
                producers.toMetaInfoSegment()?.let {
                    add(it)
                }
                pubdate.toMetaInfoSegment(postfix = "出版")?.let {
                    add(it)
                }
                pages.toMetaInfoSegment(postfix = "页")?.let {
                    add(it)
                }
            }.joinToString(" / ")
        }
    }

    Text(
        text = metaInfo,
        style = MaterialTheme.typography.bodySmall,
    )
}

private fun <T> List<T>.toMetaInfoSegment(
    n: Int = 1,
    separator: String = "",
    prefix: String = "",
    postfix: String = "",
): String? {
    return take(n).takeIf(List<T>::isNotEmpty)
        ?.joinToString(separator = separator, prefix = prefix, postfix = postfix)
}