package com.doubean.ford.data.db;

import android.annotation.SuppressLint;

import androidx.room.TypeConverter;
import androidx.room.util.StringUtil;

import com.doubean.ford.data.GroupTopic;
import com.doubean.ford.data.GroupTopicTag;
import com.doubean.ford.data.SizedPhoto;
import com.doubean.ford.data.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Date timestampToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @SuppressLint("RestrictedApi")
    @TypeConverter
    public static List<Integer> stringToIntList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return StringUtil.splitToIntList(data);
    }

    @SuppressLint("RestrictedApi")
    @TypeConverter
    public static String intListToString(List<Integer> ints) {
        return StringUtil.joinIntoString(ints);
    }

    @TypeConverter
    public static String groupTopicListToString(List<GroupTopic> topics) {
        return new Gson().toJson(topics);
    }

    @TypeConverter
    public static List<GroupTopic> stringToGroupTopicList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(data, new TypeToken<ArrayList<GroupTopic>>() {
        }.getType());
    }

    @TypeConverter
    public static String groupTabListToString(List<GroupTopicTag> ints) {
        return new Gson().toJson(ints);
    }

    @TypeConverter
    public static List<GroupTopicTag> stringToGroupTabList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(data, new TypeToken<ArrayList<GroupTopicTag>>() {
        }.getType());
    }

    @TypeConverter
    public static String stringListToString(List<String> strings) {
        return new Gson().toJson(strings);
    }

    @TypeConverter
    public static List<String> stringToStringList(String string) {
        return new Gson().fromJson(string, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    @TypeConverter
    public static User stringToUser(String strings) {
        return new Gson().fromJson(strings, User.class);
    }

    @TypeConverter
    public static String userToString(User user) {
        return new Gson().toJson(user);
    }

    @TypeConverter
    public static String sizedPhotoListToString(List<SizedPhoto> photo) {
        return new Gson().toJson(photo);
    }

    @TypeConverter
    public static List<SizedPhoto> stringToSizedPhotoList(String string) {
        return new Gson().fromJson(string, new TypeToken<ArrayList<SizedPhoto>>() {
        }.getType());
    }
}