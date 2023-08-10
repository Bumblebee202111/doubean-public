package com.doubean.ford.util

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.annotation.ColorInt

//iosched

fun parseHexColor(colorString: String): Int {
    if (colorString.isNotEmpty() && colorString[0] == '#') {
        // Use a long to avoid rollovers on #ffXXXXXX
        var color = java.lang.Long.parseLong(colorString.substring(1), 16)
        if (colorString.length == 7) {
            // Set the alpha value
            color = color or 0x00000000ff000000
        } else if (colorString.length != 9) {
            throw IllegalArgumentException("Unknown color: $colorString")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

/** Reads the color attribute from the theme for given [colorAttributeId] */
fun Context.getColorFromTheme(colorAttributeId: Int): Int {
    val typedValue = TypedValue()
    val typedArray: TypedArray =
        this.obtainStyledAttributes(
            typedValue.data, intArrayOf(colorAttributeId)
        )
    @ColorInt val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return color
}

