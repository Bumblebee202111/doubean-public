package com.doubean.ford.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun dateTimeText(localDateTime: LocalDateTime, dateTimeStyle: DateTimeStyle): String {
    return localDateTime.format(localDateTime.formatterOfStyle(dateTimeStyle))
}

fun LocalDateTime.formatterOfStyle(
    dateTimeStyle: DateTimeStyle,
): DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeStyle.getPattern(this))

enum class DateTimeStyle {
    Short {
        override fun getPattern(localDateTime: LocalDateTime): String {
            val now = LocalDateTime.now()
            return if (now.year == localDateTime.year) {
                if (now.month == localDateTime.month) {
                    if (now.dayOfMonth == localDateTime.dayOfMonth) {
                        "HH:mm"
                    } else {
                        "MM-dd"
                    }
                } else {
                    "MM-dd"
                }
            } else {
                "yyyy-MM"
            }
        }
    },
    Normal {
        override fun getPattern(localDateTime: LocalDateTime): String {
            val now = LocalDateTime.now()
            return if (now.year == localDateTime.year) {
                if (now.month == localDateTime.month) {
                    if (now.dayOfMonth == localDateTime.dayOfMonth) {
                        "HH:mm"
                    } else {
                        "MM-dd HH:mm"
                    }
                } else {
                    "MM-dd HH:mm"
                }
            } else {
                "yy-MM-dd HH:mm"
            }
        }
    },
    Long {
        override fun getPattern(localDateTime: LocalDateTime): String {
            return "yyyy-MM-dd HH:mm"
        }
    };

    abstract fun getPattern(localDateTime: LocalDateTime): String
    fun format(localDateTime: LocalDateTime): String =
        localDateTime.format(DateTimeFormatter.ofPattern(getPattern(localDateTime)))

}