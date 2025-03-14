package com.github.bumblebee202111.doubean.feature.groups.shared

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun groupTopAppBarColor(groupColor: Color): TopAppBarColors {
    val contentColor = Color.White
    return TopAppBarDefaults.topAppBarColors(
        containerColor = groupColor,
        scrolledContainerColor = groupColor,
        navigationIconContentColor = contentColor,
        titleContentColor = contentColor,
        actionIconContentColor = contentColor
    )
}