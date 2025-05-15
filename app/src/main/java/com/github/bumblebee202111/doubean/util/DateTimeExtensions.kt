package com.github.bumblebee202111.doubean.util

import android.content.Context
import com.github.bumblebee202111.doubean.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun LocalDateTime.abbreviatedDateTimeString(context: Context): String {
    val now = LocalDateTime.now()

    if (this.toLocalDate().equals(now.minusDays(1).toLocalDate())) {
        val rawFormatted = this.format(DateTimeFormatter.ofPattern("HH:mm"))
        return context.getString(R.string.date_time_yesterday_time, rawFormatted)
    }

    val pattern = if (now.year == this.year) {
        if (now.month == this.month) {
            if (now.dayOfMonth == this.dayOfMonth) {
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

    val formatted = this.format(DateTimeFormatter.ofPattern(pattern))
    return formatted
}


fun LocalDateTime.intermediateDateTimeString(): String {
    val now = LocalDateTime.now()
    val pattern = if (now.year == this.year) {
        if (now.month == this.month) {
            if (now.dayOfMonth == this.dayOfMonth) {
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
    val formatted = this.format(DateTimeFormatter.ofPattern(pattern))
    return formatted
}


fun LocalDateTime.fullDateTimeString(): String {
    val pattern = "yyyy-MM-dd HH:mm"
    val formatted = this.format(DateTimeFormatter.ofPattern(pattern))
    return formatted
}