package com.github.bumblebee202111.doubean.ui.component

import android.content.res.ColorStateList
import android.widget.RatingBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.model.Rating

@Composable
fun RatingBar(rating: Rating) {
    AndroidView(
        factory = { context ->
            RatingBar(context, null, R.attr.ratingBarStyleSmall).apply {
                stepSize = 0.1F
                secondaryProgressTintList = ColorStateList.valueOf(0)
                this@apply.rating = rating.value * 5 / rating.max
            }
        }
    )
}

@Composable
@Preview
fun RatingBarPreview() {
    RatingBar(Rating(8.6F, 10))
}