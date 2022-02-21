package com.doubean.ford.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.doubean.ford.data.db.Converters;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(tableName = "groups")
@TypeConverters(Converters.class)
public class Group {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    public final String id;

    @NonNull
    @SerializedName("name")
    public String groupName;
    @SerializedName("member_count")
    public int memberCount;
    @SerializedName("topic_count")
    public int topicCount;
    @SerializedName("create_time")
    public String dateCreated;
    @SerializedName("group_tabs")
    public List<GroupTopicTag> groupTabs;
    @SerializedName("sharing_url")
    public String sharingUrl;
    @SerializedName(value = "desc", alternate = {"desc_abstract"})
    public String desc;
    @SerializedName("avatar")
    public String avatarUrl;
    @SerializedName("background_mask_color")
    public String color;

    @SerializedName("member_name")
    public String memberName;

    public Group(@NonNull String id, @NonNull String groupName, int memberCount, int topicCount, String dateCreated, List<GroupTopicTag> groupTabs, String sharingUrl, String desc, String avatarUrl, String color) {
        this.id = id;
        this.groupName = groupName;
        this.memberCount = memberCount;
        this.topicCount = topicCount;
        this.dateCreated = dateCreated;
        this.groupTabs = groupTabs;
        this.sharingUrl = sharingUrl;
        this.desc = desc;
        this.avatarUrl = avatarUrl;
        this.color = color;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public List<GroupTopicTag> getGroupTabs() {
        return groupTabs;
    }

    public String getSharingUrl() {
        return sharingUrl;
    }

    public String getDesc() {
        return desc;
    }

    public String getColor() {
        if (color == null)
            return "#FFFFFF";
        return color;
    }

    public String getId() {
        return id;
    }

    @NonNull
    public String getGroupName() {
        return groupName;
    }

    public int getMemberCount() {
        return memberCount;
    }
    public String getShortDesc() {
        if (desc == null)
            return null;
        Matcher matcher = Pattern.compile("^(.*?)\\s").matcher(desc);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}
