package com.doubean.ford.data.db;

import android.annotation.SuppressLint;

import androidx.room.TypeConverter;
import androidx.room.util.StringUtil;

import com.doubean.ford.data.vo.Group;
import com.doubean.ford.data.vo.GroupBrief;
import com.doubean.ford.data.vo.GroupPostComment;
import com.doubean.ford.data.vo.GroupPostTag;
import com.doubean.ford.data.vo.GroupTab;
import com.doubean.ford.data.vo.SizedPhoto;
import com.doubean.ford.data.vo.User;
import com.doubean.ford.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converters {
    @TypeConverter
    public static LocalDateTime stringToLocalDateTime(String text) {
        return text == null ? null : LocalDateTime.parse(text, DateTimeFormatter.ofPattern(Constants.DATETIME_PATTERN_DEFAULT));
    }

    @TypeConverter
    public static String dateToTimestamp(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATETIME_PATTERN_DEFAULT));
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
    public static String groupPostCommentToString(GroupPostComment groupPostComment) {
        return new Gson().toJson(groupPostComment);
    }

    @TypeConverter
    public static GroupPostComment stringToGroupPostComment(String data) {
        if (data == null) {
            return null;
        }
        return new Gson().fromJson(data, GroupPostComment.class);
    }

    @TypeConverter
    public static String groupTabListToString(List<GroupTab> groupTabs) {
        return new Gson().toJson(groupTabs);
    }

    @TypeConverter
    public static List<GroupTab> stringToGroupTabList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(data, new TypeToken<ArrayList<GroupTab>>() {
        }.getType());
    }

    @TypeConverter
    public static String postTagListToString(List<GroupPostTag> groupPostTags) {
        return new Gson().toJson(groupPostTags);
    }

    @TypeConverter
    public static List<GroupPostTag> stringToPostTagList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return new Gson().fromJson(data, new TypeToken<ArrayList<GroupPostTag>>() {
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
    public static Group stringToGroup(String strings) {
        return new Gson().fromJson(strings, Group.class);
    }

    @TypeConverter
    public static String groupToString(Group group) {
        return new Gson().toJson(group);
    }

    @TypeConverter
    public static GroupBrief stringToGroupBrief(String strings) {
        return new Gson().fromJson(strings, GroupBrief.class);
    }

    @TypeConverter
    public static String groupBriefToString(GroupBrief groupBrief) {
        return new Gson().toJson(groupBrief);
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