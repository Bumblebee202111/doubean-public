package com.github.bumblebee202111.doubean.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.bumblebee202111.doubean.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Defines the formatting style for a relative date string, based on the UI context.
 */
enum class DateTimeStyle {
    /** For list items that expand to a detail screen (most concise). */
    ABBREVIATED,

    /** For list items that do not have a detail screen (more info needed). */
    INTERMEDIATE,

    /** For detail screens (most complete). */
    FULL
}

/**
 * Converts a LocalDateTime to a localized, relative string for use in @Composable functions.
 *
 * @param style The desired formatting style.
 * @param now The current time, injectable for testability.
 * @param context The context for localization, injectable for testability.
 */
@Composable
fun LocalDateTime.toRelativeString(
    style: DateTimeStyle,
    now: LocalDateTime = LocalDateTime.now(),
    context: Context = LocalContext.current,
): String {
    val daysDiff = ChronoUnit.DAYS.between(this.toLocalDate(), now.toLocalDate())

    if (daysDiff == 0L && style != DateTimeStyle.FULL) {
        return this.format(DateTimeFormatter.ofPattern("HH:mm")) // Today
    }

    // "Yesterday" is localized and only used for the abbreviated style.
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