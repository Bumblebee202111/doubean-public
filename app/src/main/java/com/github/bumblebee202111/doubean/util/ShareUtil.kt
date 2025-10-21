package com.github.bumblebee202111.doubean.util

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat.IntentBuilder

object ShareUtil {
    fun share(context: Context, shareText: CharSequence) {
        val shareIntent = IntentBuilder(context)
            .setText(shareText)
            .setType("text/plain")
            .createChooserIntent()

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        context.startActivity(shareIntent)
    }
}