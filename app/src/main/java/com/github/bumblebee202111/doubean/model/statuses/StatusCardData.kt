package com.github.bumblebee202111.doubean.model.statuses

sealed interface StatusCardData

data class UnsupportedTypeCardData(val type: String?) : StatusCardData