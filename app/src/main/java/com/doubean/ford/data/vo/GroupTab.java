package com.doubean.ford.data.vo;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class GroupTab extends GroupPostTag implements Serializable {
    public final int seq;

    public GroupTab(@Nullable String id, String name, int seq) {
        super(id, name);
        this.seq = seq;
    }
}
