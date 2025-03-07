package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

enum class DoubeanRatingBarSize(val iconSize: Dp, val spacing: Dp) {
    Small(
        iconSize = 14.dp,
        spacing = 1.dp
    ),
    Large(
        iconSize = 24.dp,
        spacing = 2.dp
    )
}

@Composable
fun DoubeanRatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    size: DoubeanRatingBarSize = DoubeanRatingBarSize.Small,
) {
    val clampedRating = rating.coerceIn(0f, 5f)
    val activeColor = Color(0xFFFFC107)
    val inactiveColor = Color(0xFF9E9E9E)

    Row(modifier) {
        repeat(5) { index ->
            val fillPercentage = (clampedRating - index).coerceIn(0f, 1f)
            RatingStar(
                fillPercentage = fillPercentage,
                size = size.iconSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
            if (index < 4) Spacer(Modifier.width(size.spacing))
        }
    }
}


@Composable
private fun RatingStar(
    fillPercentage: Float,
    size: Dp,
    activeColor: Color,
    inactiveColor: Color,
) {
    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = size.toPx()

        
        drawStar(inactiveColor, canvasSize)

        
        if (fillPercentage > 0f) {
            drawContext.canvas.save()
            drawContext.canvas.clipRect(
                left = 0f,
                top = 0f,
                right = canvasSize * fillPercentage,
                bottom = canvasSize
            )
            drawStar(activeColor, canvasSize)
            drawContext.canvas.restore()
        }
    }
}

private fun DrawScope.drawStar(color: Color, size: Float) {
    val center = size / 2
    val radius = size / 2
    val innerRadius = radius * 0.4f  

    val path = Path().apply {
        moveTo(center, 0f)  

        
        for (i in 1..10) {
            val angle = Math.PI * 2 * i / 10 - Math.PI / 2
            val r = if (i % 2 == 0) radius else innerRadius
            val x = center + (r * cos(angle)).toFloat()
            val y = center + (r * sin(angle)).toFloat()
            lineTo(x, y)
        }
        close()
    }

    drawPath(path, color)
}

@Preview(name = "Small Rating", group = "Rating Bars", showBackground = true)
@Composable
private fun PreviewSmallRating() {
    DoubeanRatingBar(
        rating = 3.5f,
        size = DoubeanRatingBarSize.Small
    )
}

@Preview(name = "Large Rating", group = "Rating Bars", showBackground = true)
@Composable
private fun PreviewLargeRating() {
    DoubeanRatingBar(
        rating = 4.2f,
        size = DoubeanRatingBarSize.Large
    )
}

@Preview(name = "Edge Cases", group = "Rating Bars")
@Composable
private fun PreviewEdgeCases() {
    Column {
        DoubeanRatingBar(rating = 5.5f)  
        DoubeanRatingBar(rating = 2.7f)  
        DoubeanRatingBar(rating = 0.4f)  
    }
}