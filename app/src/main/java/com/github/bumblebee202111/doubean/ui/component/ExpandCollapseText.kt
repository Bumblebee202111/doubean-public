package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.github.bumblebee202111.doubean.R

/**
 * A Text composable that can be expanded or collapsed. The action is only shown if
 * the text is truncated.
 *
 * @param text The full text to display.
 * @param maxLines The number of lines to show when collapsed.
 * @param style The text style for both the main text and the action link.
 * @param usesPrimaryLinkStyle If true, the action link uses the theme's primary color.
 * If false, it uses the local content color with an underline.
 */
@Composable
fun ExpandCollapseText(
    text: String,
    maxLines: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    usesPrimaryLinkStyle: Boolean = true,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var truncated by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            style = style,
            onTextLayout = { textLayoutResult ->
                truncated = textLayoutResult.didOverflowHeight
            }
        )

        if (truncated || expanded) {
            val actionTextResId = if (expanded) R.string.collapse else R.string.expand
            val linkStyle = if (usesPrimaryLinkStyle) {
                SpanStyle(color = MaterialTheme.colorScheme.primary)
            } else {
                SpanStyle(textDecoration = TextDecoration.Underline)
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = linkStyle) {
                        append(stringResource(id = actionTextResId))
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { expanded = !expanded },
                style = style
            )
        }
    }
}