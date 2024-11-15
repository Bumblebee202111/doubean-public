package com.github.bumblebee202111.doubean.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object OpenInUtils {
    fun openInBrowser(context: Context, url: String?) {
        val defaultBrowser = Intent
            .makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            .setData(Uri.parse(url))
        context.startActivity(defaultBrowser)
    }

    fun openInDouban(context: Context, uri: String?): Result<Unit> {
        return try {
            val doubanIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            context.startActivity(doubanIntent)
            Result.success(Unit)
        } catch (ex: ActivityNotFoundException) {
            Result.failure(ex)
        }

    } //https://www.douban.com/doubanapp/dispatch?uri=/group/697689

    fun viewInActivity(context: Context, uri: String?) {
        Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            context.startActivity(this)
        }
    }
}