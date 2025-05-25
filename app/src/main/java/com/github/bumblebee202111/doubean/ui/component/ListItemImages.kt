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

    val imageShape = RoundedCornerShape(8.dp)

    if (images.size == 1) {
        BoxWithConstraints {
            val image = images[0]
            val normal = image.normal
            val density = LocalDensity.current
            val originalWidthDp = with(density) {
                normal.width.toDp()
            }
            val originalHeightDp = with(density) {
                normal.height.toDp()
            }
            val goldenWidth = this.maxWidth * 0.618f
            val scale = (goldenWidth / originalWidthDp).coerceAtMost(1f)
            val displayWidth = originalWidthDp * scale
            val displayHeight = originalHeightDp * scale
            AsyncImage(
                model = normal.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(displayWidth.coerceAtMost(this.maxWidth))
                    .height(displayHeight.coerceAtMost(this.maxHeight))
                    .clip(imageShape)
                    .clickable { onImageClick(image) }
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