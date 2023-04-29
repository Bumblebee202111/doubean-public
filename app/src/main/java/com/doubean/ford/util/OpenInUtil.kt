package com.doubean.ford.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object OpenInUtil {
    fun openInBrowser(context: Context, url: String?) {
        val defaultBrowser = Intent
            .makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            .setData(Uri.parse(url))
        context.startActivity(defaultBrowser)
    }

    fun openInDouban(context: Context, uri: String?) {
        val doubanIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(doubanIntent)
    } //https://www.douban.com/doubanapp/dispatch?uri=/group/697689
}