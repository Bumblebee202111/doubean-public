package com.github.bumblebee202111.doubean.util

import android.content.Context
import com.github.bumblebee202111.doubean.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Should be applied to expandable list items
 * @param context the context for retrieving localized "yesterday" text
 */
fun LocalDateTime.abbreviatedDateTimeString(context: Context): String {
    val now = LocalDateTime.now()

    if (this.toLocalDate().equals(now.minusDays(1).toLocalDate())) { //Of yesterday
        val rawFormatted = this.format(DateTimeFormatter.ofPattern("HH:mm"))
        return context.getString(R.string.date_time_yesterday_time, rawFormatted)
    }

    val pattern = if (now.year == this.year) { //Of current year
        if (now.month == this.month) { //Of current month
            if (now.dayOfMonth == this.dayOfMonth) { //Of current date
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

/**
 * Should be applied to non-expandable list items
 */
fun LocalDateTime.intermediateDateTimeString(): String {
    val now = LocalDateTime.now()
    val pattern = if (now.year == this.year) { //Of current year
        if (now.month == this.month) { //Of current month
            if (now.dayOfMonth == this.dayOfMonth) { //Of current date
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

/**
 * Should be applied to the detail (expanded) info of a model, not in a list
 */
fun LocalDateTime.fullDateTimeString(): String {
    val pattern = "yyyy-MM-dd HH:mm"
    val formatted = this.format(DateTimeFormatter.ofPattern(pattern))
    return formatted
}