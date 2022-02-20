package com.doubean.ford.data;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("kind")
    public String type;
    public String name;
    @SerializedName("avatar")
    public String avatarUrl;
    public String id;
}
