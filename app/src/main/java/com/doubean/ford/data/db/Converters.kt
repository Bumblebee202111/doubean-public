package com.doubean.ford.data.db

import android.annotation.SuppressLint
import androidx.room.*
import com.doubean.ford.data.vo.*
import com.doubean.ford.util.Constants
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Converters {
    @TypeConverter
    fun stringToLocalDateTime(text: String?): LocalDateTime? {
        //return text == null ? null : new Gson().fromJson(text,LocalDateTime.class);
        return if (text == null) null else LocalDateTime.parse(
            text, DateTimeFormatter.ofPattern(
                Constants.DATETIME_PATTERN_DEFAULT
            )
        )
    }

    @TypeConverter
    fun dateToTimestamp(localDateTime: LocalDateTime?): String? {
        //return localDateTime == null ? null : new Gson().toJson(localDateTime);
        return localDateTime?.format(DateTimeFormatter.ofPattern(Constants.DATETIME_PATTERN_DEFAULT))
    }

    @TypeConverter
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toInt()
                } catch (ex: NumberFormatException) {
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    fun intListToString(ints: List<Int?>?): String? {
        return ints?.joinToString(",")
    }

    @TypeConverter
    fun stringToLongList(data: String?): List<Long>? {
        return if (data == null) {
            null
        } else Gson().fromJson(
            data,
            object : TypeToken<ArrayList<Long?>?>() {}.type
        )
    }

    @SuppressLint("RestrictedApi")
    @TypeConverter
    fun longListToString(longs: List<Long?>?): String {
        return Gson().toJson(longs)
    }

    @TypeConverter
    fun postCommentToString(postComment: PostComment?): String {
        val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
            JsonSerializer { localDateTime: LocalDateTime, _: Type?, _: JsonSerializationContext? ->
                JsonPrimitive(
                    localDateTime.format(
                        DateTimeFormatter.ofPattern(Constants.DATETIME_PATTERN_DEFAULT)
                    )
                )
            } as JsonSerializer<LocalDateTime>).create()
        return gson.toJson(postComment)
    }

    @TypeConverter
    fun stringToPostComment(data: String?): PostComment? {
        if (data == null) {
            return null
        }
        val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
            JsonDeserializer { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                LocalDateTime.parse(
                    json.asJsonPrimitive.asString,
                    DateTimeFormatter.ofPattern(Constants.DATETIME_PATTERN_DEFAULT)
                )
            } as JsonDeserializer<LocalDateTime>).create()
        return gson.fromJson(data, PostComment::class.java)
    }

    @TypeConverter
    fun groupTabListToString(groupTabs: List<GroupTab?>?): String {
        return Gson().toJson(groupTabs)
    }

    @TypeConverter
    fun stringToGroupTabList(data: String?): List<GroupTab> {
        return if (data == null) {
            emptyList()
        } else Gson().fromJson(
            data,
            object : TypeToken<ArrayList<GroupTab?>?>() {}.type
        )
    }

    @TypeConverter
    fun postTagListToString(groupPostTags: List<GroupPostTag?>?): String {
        return Gson().toJson(groupPostTags)
    }

    @TypeConverter
    fun stringToPostTagList(data: String?): List<GroupPostTag> {
        return if (data == null) {
            emptyList()
        } else Gson().fromJson(
            data,
            object : TypeToken<ArrayList<GroupPostTag?>?>() {}.type
        )
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
    fun stringToUser(string: String?): User {
        return Gson().fromJson(string, User::class.java)
    }

    @TypeConverter
    fun userToString(user: User?): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun stringToGroup(string: String?): Group? {
        return Gson().fromJson(string, Group::class.java)
    }

    @TypeConverter
    fun groupToString(group: Group?): String {
        return Gson().toJson(group)
    }

    @TypeConverter
    fun stringToGroupBrief(strings: String?): GroupBrief? {
        return Gson().fromJson(strings, GroupBrief::class.java)
    }

    @TypeConverter
    fun groupBriefToString(groupBrief: GroupBrief?): String {
        return Gson().toJson(groupBrief)
    }

    @TypeConverter
    fun sizedPhotoListToString(photo: List<SizedPhoto>?): String? {
        return Gson().toJson(photo)
    }

    @TypeConverter
    fun stringToSizedPhotoList(string: String?): List<SizedPhoto> {
        return Gson().fromJson(string, object : TypeToken<ArrayList<SizedPhoto>?>() {}.type)
    }
}