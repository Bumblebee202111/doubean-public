package com.doubean.ford.util

import android.content.Context
import kotlin.math.ceil

object SpanCountCalculator {
    fun getSpanCount(context: Context, minSpanWidthDps: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val displayWidthDps = displayMetrics.widthPixels / displayMetrics.density
        return ceil((displayWidthDps / minSpanWidthDps).toDouble()).toInt()
    }
}