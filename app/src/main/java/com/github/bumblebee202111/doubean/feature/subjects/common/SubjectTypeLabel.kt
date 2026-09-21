package com.github.bumblebee202111.doubean.feature.subjects.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.bumblebee202111.doubean.core.theme.DoubeanTheme
import com.github.bumblebee202111.doubean.shared.subject.model.SubjectType

@Composable
fun SubjectTypeLabel(type: SubjectType) {
    type.typeNameResId?.let {
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