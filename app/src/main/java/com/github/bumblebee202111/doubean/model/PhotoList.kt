package com.github.bumblebee202111.doubean.model

data class PhotoList(
    val count: Int,
    val photos: List<Photo>,
    val start: Int,
    val total: Int,
)
