package com.github.bumblebee202111.doubean.model.subjects

data class CreditList(
    val items: List<Celebrity>,
    val total: Int,
)

data class Celebrity(
    val id: String,
    val name: String,
    val character: String,
    val avatarUrl: String,
)