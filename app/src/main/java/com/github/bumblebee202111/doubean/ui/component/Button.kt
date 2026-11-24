package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@Composable
private fun doubeanButtonShapes(): ButtonShapes = ButtonDefaults.shapes(
    shape = MaterialTheme.shapes.small,
    pressedShape = MaterialTheme.shapes.extraSmall,
)

@Composable
fun DoubeanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        shapes = doubeanButtonShapes(),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        border = border,
        content = content
    )
}

@Composable
fun DoubeanTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        shapes = doubeanButtonShapes(),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        border = border,
        content = content
    )
}

@Composable
fun DoubeanOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        shapes = doubeanButtonShapes(),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        border = border,
        content = content
    )
}

@Composable
fun DoubeanTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        shapes = doubeanButtonShapes(),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        border = border,
        content = content
    )
}

@Composable
@ReadOnlyComposable
fun SegmentedButtonDefaults.doubeanItemShape(
    index: Int,
    count: Int,
    baseShape: CornerBasedShape = MaterialTheme.shapes.small,
): Shape = itemShape(index, count, baseShape)

@Composable
fun InfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = contentDescription,
            tint = if (enabled) tint else Color.Unspecified
        )
    }
}