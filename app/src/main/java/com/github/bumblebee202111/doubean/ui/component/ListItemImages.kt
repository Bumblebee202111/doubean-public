package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.bumblebee202111.doubean.model.SizedImage

@OptIn(ExperimentalLayoutApi::class)
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
            AsyncImage(
                model = images[0].normal.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clickable { onImageClick(images[0]) }
                    .sizeIn(
                        maxWidth = maxWidth * 0.618f,
                        maxHeight = maxHeight * 0.618f
                    )
                //.fillMaxWidth(0.618f)
                // .fillMaxHeight(0.618f)
                //.weight(1f)
                //.aspectRatio(1f)
            )
        }


    } else {
        FlowRow(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 3
        ) {

            images.forEach { image ->
                AsyncImage(
                    model = image.normal.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable { onImageClick(image) }
                        .fillMaxWidth()
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }

    }
}