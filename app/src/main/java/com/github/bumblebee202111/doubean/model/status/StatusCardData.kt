package com.github.bumblebee202111.doubean.model.status

sealed interface StatusCardData

data class UnsupportedTypeCardData(val type: String?) : StatusCardData