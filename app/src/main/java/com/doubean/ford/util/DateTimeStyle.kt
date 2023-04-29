package com.doubean.ford.util

import java.time.LocalDateTime

enum class DateTimeStyle : IDateTimeStyle {
    SHORT {
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
    NORMAL {
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
    LONG {
        override fun getPattern(localDateTime: LocalDateTime): String {
            return "yyyy-MM-dd HH:mm"
        }
    }
}