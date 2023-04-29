package com.doubean.ford.util

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.doubean.ford.R

object TopItemNoBackgroundUtil {
    @ColorInt
    fun getNoBackground(context: Context, curr: Int, total: Int): Int {
        @ColorInt val topItemColor = context.getColor(R.color.top_item)
        @ColorInt val yellow = context.getColor(R.color.douban_yellow)
        return ColorUtils.blendARGB(topItemColor, yellow, curr / (total - 1f))
    }
}