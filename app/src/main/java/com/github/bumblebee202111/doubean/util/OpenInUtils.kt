package com.github.bumblebee202111.doubean.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object OpenInUtils {
    fun openInBrowser(context: Context, url: String?) {
        val defaultBrowser = Intent
            .makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            .setData(url?.toUri())
        context.startActivity(defaultBrowser)
    }

    fun openInDouban(context: Context, uri: String?): Result<Unit> {
        return try {
            val doubanIntent = Intent(Intent.ACTION_VIEW, uri?.toUri())
            context.startActivity(doubanIntent)
            Result.success(Unit)
        } catch (ex: ActivityNotFoundException) {
            Result.failure(ex)
        }

    } 

    fun viewInActivity(context: Context, uri: String?) {
        Intent(Intent.ACTION_VIEW, uri?.toUri()).apply {
            context.startActivity(this)
        }
    }
}