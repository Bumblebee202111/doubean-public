package com.doubean.ford.util

import android.text.InputFilter
import android.text.Spanned

//Adapted from https://stackoverflow.com/a/19072151
class MinMaxEditTextInputFilter(private val mMin: Int, private val mMax: Int) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int,
    ): CharSequence {
        try {
            val newValueString: String = dest.subSequence(0, dstart).toString() +
                    source.subSequence(start, end) +
                    dest.subSequence(dend, dest.length)
            val newValueInt = newValueString.toInt()
            if (isInRange(mMin, mMax, newValueInt)) return source
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(min: Int, max: Int, value: Int): Boolean {
        return if (max > min) {
            value in min..max
        } else {
            value in max..min
        }
    }
}