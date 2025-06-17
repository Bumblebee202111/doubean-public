package com.github.bumblebee202111.doubean.feature.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.model.fangorns.User

@Composable
fun UserDrawerHeader(
    currentUser: User?,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onHeaderClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentUser == null) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Login",
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text("Login", style = MaterialTheme.typography.titleMedium)
        } else {
            AsyncImage(
                model = currentUser.avatar,
                contentDescription = "Me",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(currentUser.name, style = MaterialTheme.typography.titleMedium)
                Text(currentUser.uid, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
