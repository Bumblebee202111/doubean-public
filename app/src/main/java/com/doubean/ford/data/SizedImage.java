package com.doubean.ford.data;

import com.google.gson.annotations.SerializedName;

public class SizedImage {
    @SerializedName("is_animated")
    public boolean isAnimated;
    public ImageItem large;
    public ImageItem normal;
    public ImageItem raw;
    public ImageItem small;
    public ImageItem video;

    public static class ImageItem {
        public int height;
        public long size;
        public String url;
        public int width;
    }
}
