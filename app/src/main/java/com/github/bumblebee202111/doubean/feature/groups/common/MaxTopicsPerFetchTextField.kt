package com.github.bumblebee202111.doubean.feature.groups.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MaxTopicsPerFetchTextField(
    maxTopicsPerFetch: Int,
    onUpdateMaxTopicsPerFetch: (intValue: Int) -> Unit,
    enabled: Boolean = true,
    requestFocus: Boolean = false,
    fillMaxWidth: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var maxTopicsPerFetchValue by rememberSaveable {
        mutableStateOf(maxTopicsPerFetch.toString())
    }
    TextField(
        value = maxTopicsPerFetchValue,
        onValueChange = { value ->
            if (value.isBlank()) {
                maxTopicsPerFetchValue = ""
            } else {
                value.toIntOrNull()?.takeIf { it in 1..50 }
                    ?.let {
                        maxTopicsPerFetchValue = value
                        onUpdateMaxTopicsPerFetch(it)
                    }
            }
        },
        modifier = Modifier
            .run {
                if (fillMaxWidth) {
                    this@run.fillMaxWidth()
                } else {
                    widthIn(1.dp)
                }
            }
            .run {
                if (requestFocus) {
                    this@run.focusRequester(focusRequester)
                } else {
                    this
                }
            },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                
            }
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        )
    )
    if (requestFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}