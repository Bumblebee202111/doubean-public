package com.github.bumblebee202111.doubean.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


enum class DateTimeStyle {

    ABBREVIATED,


    INTERMEDIATE,


    FULL
}


@Composable
fun LocalDateTime.toRelativeString(
    style: DateTimeStyle,
    now: LocalDateTime = LocalDateTime.now(),
    context: Context = LocalContext.current,
): String {
    val daysDiff = ChronoUnit.DAYS.between(this.toLocalDate(), now.toLocalDate())

    if (daysDiff == 0L && style != DateTimeStyle.FULL) {
        return this.format(DateTimeFormatter.ofPattern("HH:mm"))
    }


    if (daysDiff == 1L && style == DateTimeStyle.ABBREVIATED) {
        val time = this.format(DateTimeFormatter.ofPattern("HH:mm"))
        return context.getString(R.string.date_time_yesterday_time, time)
    }

    val pattern = when (style) {
        DateTimeStyle.ABBREVIATED -> if (this.year == now.year) "MM-dd" else "yyyy-MM"
        DateTimeStyle.INTERMEDIATE -> if (this.year == now.year) "MM-dd HH:mm" else "yy-MM-dd HH:mm"
        DateTimeStyle.FULL -> "yyyy-MM-dd HH:mm:ss"
    }

    return this.format(DateTimeFormatter.ofPattern(pattern))
}