package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class GroupItem {
    @PrimaryKey
    @NonNull
    public String id;

    @NonNull
    public String name;

    @SerializedName("member_count")
    public Integer memberCount;

    @SerializedName("topic_count")
    public Integer postCount;

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

    public GroupItem() {
    }

}
