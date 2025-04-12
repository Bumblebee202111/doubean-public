package com.github.bumblebee202111.doubean.model.statuses

import com.github.bumblebee202111.doubean.model.SizedImage

data class Topic(
    val ownerUri: String,
    val ownerName: String,
    val subtitle: String,
    val title: String,
    val images: List<SizedImage>,
    val id: String,
) : StatusCardData