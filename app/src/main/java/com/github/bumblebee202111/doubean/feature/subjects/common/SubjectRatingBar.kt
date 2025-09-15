package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.ui.component.DoubeanRatingBar
import com.github.bumblebee202111.doubean.ui.component.DoubeanRatingBarSize


enum class SubjectRatingBarSize {
    Compact,
    Expanded
}

@Composable
fun SubjectRatingBar(
    rating: Rating,
    modifier: Modifier = Modifier,
    size: SubjectRatingBarSize = SubjectRatingBarSize.Compact,
) {
    val ratingSize = when (size) {
        SubjectRatingBarSize.Compact -> DoubeanRatingBarSize.Small
        SubjectRatingBarSize.Expanded -> DoubeanRatingBarSize.Large
    }

    val textStyle = when (size) {
        SubjectRatingBarSize.Compact -> MaterialTheme.typography.bodySmall
        SubjectRatingBarSize.Expanded -> MaterialTheme.typography.bodyMedium
    }

    val textSpacing = ratingSize.spacing * 2
    val currentLocalContentColor = LocalContentColor.current
    when (rating) {
        is Rating.NonNull -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DoubeanRatingBar(
                    rating = normalizeRating(rating.value, rating.max),
                    size = ratingSize
                )
                Spacer(Modifier.width(textSpacing))
                Text(
                    text = "%.1f".format(rating.value),
                    style = textStyle,
                    color = currentLocalContentColor.copy(alpha = 0.9f),
                    maxLines = 1
                )
            }
        }

        is Rating.Null -> {
            Text(
                text = rating.reason.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.rating_zero),
                style = textStyle,
                color = currentLocalContentColor.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }

}

private fun normalizeRating(value: Float, max: Int): Float {
    if (max <= 0) return 0f
    return (value / max * 5).coerceIn(0f, 5f)
}

@Preview
@Composable
private fun PreviewSubjectRatingBar() {
    MaterialTheme {
        Column {
            SubjectRatingBar(
                rating = Rating.NonNull(8.4f, 10),
                size = SubjectRatingBarSize.Compact
            )
            SubjectRatingBar(
                rating = Rating.Null("暂无评分"),
                size = SubjectRatingBarSize.Expanded
            )
        }
    }
}
