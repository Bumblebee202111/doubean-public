package com.github.bumblebee202111.doubean.ui.component

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <OptionT, ValueT> SortByDropDownMenu(
    options: List<OptionT>, // List of options (e.g., SortTopicsByOption.entries)
    initialSelectedValue: ValueT, // Currently selected domain value (e.g., TopicSortBy.NEW)
    onOptionSelected: (ValueT) -> Unit,
    optionText: @Composable (OptionT) -> String,
    optionToValue: (OptionT) -> ValueT,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    // Find the option corresponding to the selected domain value
    val selectedOption by remember(initialSelectedValue) {
        derivedStateOf {
            options.first { optionToValue(it) == initialSelectedValue }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextButton(
            onClick = { /* Handled by ExposedDropdownMenuBox */ },
            modifier = Modifier.menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                enabled = enabled
            ),
            enabled = enabled
        ) {
            Text(text = optionText(selectedOption))
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionText(option)) },
                    onClick = {
                        expanded = false
                        onOptionSelected(optionToValue(option))
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}