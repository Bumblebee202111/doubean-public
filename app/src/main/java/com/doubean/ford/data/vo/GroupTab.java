package com.doubean.ford.data.vo;

import androidx.annotation.Nullable;

public class GroupTab extends GroupPostTag {
    public final int seq;

    public GroupTab(@Nullable String id, String name, int seq) {
        super(id, name);
        this.seq = seq;
    }
}
