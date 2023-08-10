package com.doubean.ford.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * From architecture-samples-views
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(
    snackbarText: String,
    timeLength: Int,
    actionResId: Int? = null,
    actionListener: View.OnClickListener? = null,
) {
    Snackbar.make(this, snackbarText, timeLength).also {
        if (actionResId != null)
            it.setAction(actionResId, actionListener)
    }.show()
}

fun View.showSnackbar(
    snackbarTextResource: Int, timeLength: Int, actionResId: Int? = null,
    actionListener: View.OnClickListener? = null,
) {
    showSnackbar(context.getString(snackbarTextResource), timeLength, actionResId, actionListener)
}