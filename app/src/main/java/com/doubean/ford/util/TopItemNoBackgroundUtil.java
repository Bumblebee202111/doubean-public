package com.doubean.ford.util;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

import com.doubean.ford.R;

public class TopItemNoBackgroundUtil {
    public static @ColorInt
    int getNoBackground(Context context, int curr, int total) {
        @ColorInt int topItemColor = context.getColor(R.color.top_item);
        @ColorInt int yellow = context.getColor(R.color.douban_yellow);
        return ColorUtils.blendARGB(topItemColor, yellow, curr / (total - 1F));
    }
}
