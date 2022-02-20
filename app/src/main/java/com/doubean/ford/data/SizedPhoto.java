package com.doubean.ford.data;

import com.google.gson.annotations.SerializedName;

public class SizedPhoto {
    public String description;
    public String id;
    public SizedImage images;
    public boolean origin;
    @SerializedName("tag_name")
    public String tag;

}
