package com.github.bumblebee202111.doubean.feature.login

import kotlinx.serialization.Serializable

@Serializable
data class VerifyPhoneRoute(
    val userId: String,
)