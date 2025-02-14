package com.github.bumblebee202111.doubean.feature.groups.common

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MaxTopicsPerFetchTextField(
    maxTopicsPerFetch: Int,
    onUpdateMaxTopicsPerFetch: (intValue: Int) -> Unit,
    enabled: Boolean,
) {
    //Spacer(modifier = Modifier.weight(1f))
    val keyboardController = LocalSoftwareKeyboardController.current
    var maxTopicsPerFetchValue by rememberSaveable {
        mutableStateOf(maxTopicsPerFetch.toString())
    }
    OutlinedTextField(
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
        modifier = Modifier.widthIn(1.dp),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                // do something here
            }
        ),
        singleLine = true,
    )
}