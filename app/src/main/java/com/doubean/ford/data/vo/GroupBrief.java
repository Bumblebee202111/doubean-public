package com.doubean.ford.data.vo;

import android.graphics.Color;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class GroupBrief {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public String id;

    @NonNull
    @SerializedName("name")
    public String name;

    @SerializedName("member_count")
    public int memberCount;

    @SerializedName("topic_count")
    public int postCount;

    @SerializedName("create_time")
    public LocalDateTime dateCreated;

    @SerializedName("sharing_url")
    public String url;

    @SerializedName("avatar")
    public String avatarUrl;

    @SerializedName("member_name")
    public String memberName;

    @SerializedName("desc_abstract")
    public String shortDescription;

    @SerializedName("background_mask_color")
    public String colorString;

    @Ignore
    public int getColor() {
        return Color.parseColor(TextUtils.isEmpty(colorString) ? "#FFFFFF" : colorString);
    }

}
