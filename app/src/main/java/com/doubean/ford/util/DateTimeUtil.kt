package com.doubean.ford.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtil {
    @JvmStatic
    fun dateTimeText(localDateTime: LocalDateTime?, dateTimeStyle: IDateTimeStyle): String? {
        return localDateTime?.format(
            DateTimeFormatter.ofPattern(
                dateTimeStyle.getPattern(localDateTime)
            )
        )
    }
}