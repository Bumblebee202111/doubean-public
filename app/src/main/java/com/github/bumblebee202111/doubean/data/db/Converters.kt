package com.github.bumblebee202111.doubean.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.github.bumblebee202111.doubean.model.SizedImage
import com.github.bumblebee202111.doubean.model.SizedPhoto
import com.github.bumblebee202111.doubean.util.DATETIME_PATTERN_DEFAULT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@ProvidedTypeConverter
class Converters(private val json: Json) {
    @Inject

    @TypeConverter
    fun stringToLocalDateTime(text: String?): LocalDateTime? {
        return if (text == null) null else LocalDateTime.parse(
            text, DateTimeFormatter.ofPattern(
                DATETIME_PATTERN_DEFAULT
            )
        )
    }
    @TypeConverter
    fun localDateTimeToString(localDateTime: LocalDateTime?): String? {
        return localDateTime?.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN_DEFAULT))
    }
    @TypeConverter
    fun stringListToString(strings: List<String>?): String? {
        return strings?.joinToString()
    }

    @TypeConverter
    fun stringToStringList(string: String?): List<String>? {
        return string?.split(", ")
    }

    @TypeConverter
    fun sizedPhotoListToString(photos: List<SizedPhoto>?): String {
        return json.encodeToString(photos)
    }

    @TypeConverter
    fun stringToSizedPhotoList(string: String?): List<SizedPhoto>? {
        return string?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun sizedImageListToString(images: List<SizedImage>?): String {
        return json.encodeToString(images)
    }

    @TypeConverter
    fun stringToSizedImageList(string: String?): List<SizedImage>? {
        return string?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}