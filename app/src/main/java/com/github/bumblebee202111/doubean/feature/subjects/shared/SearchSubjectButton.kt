package com.github.bumblebee202111.doubean.feature.subjects.shared

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun SearchSubjectButton(onClick: () -> Unit, @StringRes hintRes: Int) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = null)
        Text(text = stringResource(id = hintRes))
    }

}