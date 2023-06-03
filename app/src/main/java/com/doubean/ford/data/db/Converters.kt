package com.doubean.ford.data.db

import androidx.room.TypeConverter
import com.doubean.ford.model.SizedPhoto
import com.doubean.ford.util.DATETIME_PATTERN_DEFAULT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Converters {
    @TypeConverter
    fun stringToLocalDateTime(text: String?): LocalDateTime? {
        return if (text == null) null else LocalDateTime.parse(
            text, DateTimeFormatter.ofPattern(
                DATETIME_PATTERN_DEFAULT
            )
        )
    }

    @TypeConverter
    fun dateToTimestamp(localDateTime: LocalDateTime?): String? {
        return localDateTime?.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN_DEFAULT))
    }

    @TypeConverter
    fun stringListToString(strings: List<String?>?): String {
        return Gson().toJson(strings)
    }

    @TypeConverter
    fun stringToStringList(string: String?): List<String> {
        return Gson().fromJson(string, object : TypeToken<ArrayList<String?>?>() {}.type)
    }

    @TypeConverter
    fun sizedPhotoListToString(photo: List<SizedPhoto>?): String? {
        return Gson().toJson(photo)
    }

    @TypeConverter
    fun stringToSizedPhotoList(string: String?): List<SizedPhoto> {
        return Gson().fromJson(string, object : TypeToken<ArrayList<SizedPhoto>?>() {}.type)
    }

    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}