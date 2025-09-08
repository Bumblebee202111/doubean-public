package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.github.bumblebee202111.doubean.R
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoubeanAppBarWithSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onFocusChanged: (isFocused: Boolean) -> Unit,
    placeholderText: String,
) {
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(initialText = query)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }


    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text.toString() != query) {
            onQueryChange(textFieldState.text.toString())
        }
    }


    LaunchedEffect(query) {
        if (textFieldState.text.toString() != query) {
            textFieldState.setTextAndPlaceCursorAtEnd(query)
        }
    }


    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    onFocusChanged(focusState.isFocused)
                },
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = {
                onSearch(it)
                keyboardController?.hide()
            },
            placeholder = { Text(placeholderText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(onClick = { textFieldState.setTextAndPlaceCursorAtEnd("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.content_description_clear_search)
                        )
                    }
                }
            }
        )
    }

    AppBarWithSearch(
        state = searchBarState,
        inputField = inputField,
        navigationIcon = { BackButton(onClick = onBackClick) }
    )
}