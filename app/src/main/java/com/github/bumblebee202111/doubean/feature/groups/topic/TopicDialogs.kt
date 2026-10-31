package com.github.bumblebee202111.doubean.feature.groups.topic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.bumblebee202111.doubean.R

@Composable
fun JumpToCommentOfIndexDialog(
    currentCommentIndex: Int,
    commentCount: Int,
    onDismissRequest: () -> Unit,
    jumpToCommentOfIndex: (newCommentIndex: Int) -> Unit,
) {
    if (commentCount <= 1) {
        onDismissRequest()
        return
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            JumpToCommentOfIndexSlider(
                initialCommentIndex = currentCommentIndex,
                maxCommentIndex = commentCount - 1,
                onTargetCommentIndexConfirmed = {
                    onDismissRequest()
                    jumpToCommentOfIndex(it)
                }
            )
        }
    }
}

@Composable
fun JumpToCommentOfIndexSlider(
    initialCommentIndex: Int,
    maxCommentIndex: Int,
    onTargetCommentIndexConfirmed: (newCommentIndex: Int) -> Unit,
) {
    var sliderPosition by rememberSaveable(initialCommentIndex) {
        mutableFloatStateOf(
            initialCommentIndex.toFloat()
        )
    }
    val steps = (maxCommentIndex - 1).coerceAtLeast(0)
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.jump_to_comment),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            steps = steps,
            valueRange = 0f..maxCommentIndex.toFloat(),
            onValueChangeFinished = {
                onTargetCommentIndexConfirmed(sliderPosition.toInt())
            },
        )
        Text(
            text = "${sliderPosition.toInt() + 1} / ${maxCommentIndex + 1}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
fun JumpToCommentSliderPreview() {
    JumpToCommentOfIndexSlider(100, 1000) {}
}