package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.bumblebee202111.doubean.model.SizedImage

@Composable
fun ListItemImages(
    images: List<SizedImage>,
    modifier: Modifier = Modifier,
    onImageClick: (imageUrl: SizedImage) -> Unit = {},
) {
    if (images.isEmpty()) return
    if (images.size == 1) {
        BoxWithConstraints(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
        ) {
            val normal = images[0].normal
            val density = LocalDensity.current
            val originalWidth = with(density) {
                normal.width.toDp()
            }
            val originalHeight = with(density) {
                normal.height.toDp()
            }
            val goldenWidth = maxWidth * 0.618f
            val scale = (goldenWidth / originalWidth).coerceAtMost(1f)
            AsyncImage(
                model = normal.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clickable { onImageClick(images[0]) }




                    .width(originalWidth * scale)
                    .height(originalHeight * scale)
            )
        }


    } else {
        
        
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            images.take(3).forEach { image ->
                AsyncImage(
                    model = image.normal.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable { onImageClick(image) }
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }

    }
}