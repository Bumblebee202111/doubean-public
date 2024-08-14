package com.github.bumblebee202111.doubean.feature.groups.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun NotificationsButton(
    groupColor: Color,
    enableNotifications: Boolean,
    showPrefDialog: () -> Unit,
) {
    OutlinedIconToggleButton(
        checked = enableNotifications,
        onCheckedChange = {
            showPrefDialog()
        },
        colors = IconButtonDefaults.outlinedIconToggleButtonColors(
            contentColor = groupColor
        )
    ) {
        Icon(
            imageVector = if (enableNotifications) {
                Icons.Filled.Notifications
            } else {
                Icons.Filled.NotificationAdd
            },
            contentDescription = null
        )
    }
}