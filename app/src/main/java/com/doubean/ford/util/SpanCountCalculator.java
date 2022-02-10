package com.doubean.ford.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class SpanCountCalculator {
    public static int getSpanCount(Context context, int minSpanWidthDps) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float displayWidthDps = displayMetrics.widthPixels / displayMetrics.density;
        return (int) Math.ceil(displayWidthDps / minSpanWidthDps);
    }
}
