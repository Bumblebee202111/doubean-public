package com.github.bumblebee202111.doubean.feature.subjects.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.subjects.SubjectType
import com.github.bumblebee202111.doubean.ui.theme.DoubeanTheme

@Composable
fun SubjectTypeLabel(type: SubjectType) {
    val subjectTypeNameResId = when (type) {
        SubjectType.MOVIE -> R.string.movie
        SubjectType.TV -> R.string.tv
        SubjectType.BOOK -> R.string.book
        SubjectType.UNSUPPORTED -> null
    }
    subjectTypeNameResId?.let {
        Text(
            text = stringResource(id = it),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
@Preview
private fun SubjectTypeLabelPreview() {
    DoubeanTheme {
        Surface {
            SubjectTypeLabel(type = SubjectType.BOOK)
        }
    }
}