package com.doubean.ford.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * From architecture-samples-views
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).show()
}

fun View.showSnackbar(snackbarTextResource: Int, timeLength: Int) {
    showSnackbar(context.getString(snackbarTextResource), timeLength)
}