package com.doubean.ford.data.vo;

import com.google.gson.annotations.SerializedName;

public class SizedPhoto {
    public String description;
    public String id;
    public SizedImage image;
    public boolean origin;
    @SerializedName("tag_name")
    public String tag;

}
