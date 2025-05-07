package com.github.bumblebee202111.doubean.ui.component

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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import com.github.bumblebee202111.doubean.R

@Composable
fun ExpandCollapseText(
    text: String,
    maxLines: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    usesPrimaryLinkSpanStyle: Boolean = true,
) {
    Column(modifier = modifier) {
        var expanded by remember {
            mutableStateOf(false)
        }
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            style = style
        )
        val expandCollapseStringResId = if (expanded) R.string.collapse else R.string.expand
        val annotatedString = buildAnnotatedString {
            withLink(
                LinkAnnotation.Clickable(
                    tag = "expandCollapse",
                    styles = if (usesPrimaryLinkSpanStyle) {
                        TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    } else null
                ) {
                    expanded = !expanded
                }) {
                append(stringResource(id = expandCollapseStringResId))
            }
        }
        Text(text = annotatedString, modifier = Modifier.align(Alignment.End), style = style)
    }
}