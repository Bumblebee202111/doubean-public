package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.model.subjects.Music
import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.Subject
import com.github.bumblebee202111.doubean.model.subjects.SubjectType

@Composable
fun SimpleSubjectItemContent(subject: Subject) {
    val subtitle = (subject as? Music)?.singer?.joinToString("/")?.takeIf { it.isNotEmpty() }
    SimpleSubjectItemContent(
        imageUrl = subject.imageUrl,
        title = subject.title,
        rating = subject.rating,
        subtitle = subtitle,
        isSquare = subject.type == SubjectType.MUSIC
    )
}

@Composable
fun SimpleSubjectItemContent(
    imageUrl: String,
    title: String,
    rating: Rating,
    subtitle: String? = null,
    isSquare: Boolean = false,
) {
    Column(modifier = Modifier.width(100.dp)) {
        val imageModifier = if (isSquare) {
            Modifier.size(100.dp)
        } else {
            Modifier.size(width = 100.dp, height = 140.dp) 
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = imageModifier.clip(SubjectRoundedCornerShape),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = title,
            modifier = Modifier
                .padding(top = 4.dp),
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        SubjectRatingBar(
            rating = rating,
            modifier = Modifier.padding(top = if (subtitle != null) 2.dp else 4.dp),
            size = SubjectRatingBarSize.Compact,
        )
    }
}