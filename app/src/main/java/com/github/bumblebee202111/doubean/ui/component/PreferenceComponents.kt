package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SwitchPreferenceItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    summary: String? = null,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { summary?.let { Text(it) } },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    )
}

@Composable
fun ClickablePreferenceItem(
    title: String,
    onClick: () -> Unit,
    summary: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { summary?.let { Text(it) } },
        trailingContent = trailingContent,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun PreferenceDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 0.5.dp
    )
}

@Composable
fun RadioButtonItem(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview
@Composable
fun PreferenceComponentsPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            
            PreferenceCategoryHeader(title = "General Settings")
            SwitchPreferenceItem(
                title = "Enable Notifications",
                checked = true,
                onCheckedChange = {},
                summary = "Receive app notifications"
            )
            PreferenceDivider()

            
            PreferenceCategoryHeader(title = "About")
            ClickablePreferenceItem(
                title = "App Version",
                trailingContent = { Text("1.0.0 (100)") },
                onClick = {}
            )
            ClickablePreferenceItem(
                title = "Rate App",
                summary = "Share your feedback",
                onClick = {}
            )
            PreferenceDivider()

            
            PreferenceCategoryHeader(title = "Support")
            ClickablePreferenceItem(
                title = "Contact Us",
                onClick = {},
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            )
        }
    }
}