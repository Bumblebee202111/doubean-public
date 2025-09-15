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
import com.github.bumblebee202111.doubean.model.subjects.Rating
import com.github.bumblebee202111.doubean.model.subjects.Subject

@Composable
fun SimpleSubjectRowItemContent(subject: Subject) {
    // This function now simply calls the more generic version.
    SimpleSubjectRowItemContent(
        imageUrl = subject.imageUrl,
        title = subject.title,
        rating = subject.rating
    )
}

@Composable
fun SimpleSubjectRowItemContent(imageUrl: String, title: String, rating: Rating) {
    Column(modifier = Modifier.width(100.dp)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(width = 100.dp, height = 140.dp)// Douban app uses 75x105
                .clip(SubjectRoundedCornerShape),
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
        SubjectRatingBar(
            rating = rating,
            modifier = Modifier.padding(top = 4.dp),
            size = SubjectRatingBarSize.Compact,
        )
    }
}