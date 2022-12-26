package com.doubean.ford.data.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GroupFollowItem {

    private final String groupMemberName;
    private final Integer groupMemberCount;
    @NonNull
    private final String groupId;
    private final String groupName;
    private final String groupAvatarUrl;
    private final String groupTabName;
    private final String groupColor;
    @Nullable
    private String groupTabId;

    public GroupFollowItem(@NonNull String groupId, @Nullable String groupTabId, @Nullable GroupDetail group) {
        this.groupId = groupId;
        this.groupTabId = groupTabId;
        if (group != null) {
            this.groupName = group.name;
            this.groupAvatarUrl = group.avatarUrl;
            if (groupTabId != null) {
                this.groupTabName = findGroupTabName(group);
            } else {
                groupTabName = null;
            }
            this.groupColor = group.colorString;
            this.groupMemberCount = group.memberCount;
            this.groupMemberName = group.memberName;
        } else {
            this.groupName = groupId;
            this.groupTabName = groupTabId;
            this.groupMemberCount = null;
            this.groupMemberName = null;
            this.groupColor = null;
            this.groupAvatarUrl = null;
        }
    }

    private String findGroupTabName(GroupDetail group) {
        for (GroupPostTag tab : group.tabs) {
            if (tab.id.equals(groupTabId)) {
                return tab.name;
            }
        }
        return null;
    }

    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public String getGroupAvatarUrl() {
        return groupAvatarUrl;
    }

    public String getGroupTabName() {
        return groupTabName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupColor() {
        return groupColor;
    }

    public String getGroupMemberName() {
        return groupMemberName;
    }

    public Integer getGroupMemberCount() {
        return groupMemberCount;
    }


    public String getGroupTabId() {
        return groupTabId;
    }

    public void setGroupTabId(String groupTabId) {
        this.groupTabId = groupTabId;
    }
}
