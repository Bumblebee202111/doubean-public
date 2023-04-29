package com.doubean.ford.util

import java.time.LocalDateTime

interface IDateTimeStyle {
    fun getPattern(localDateTime: LocalDateTime): String
}