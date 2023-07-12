package com.doubean.ford.util

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders

class DoubanGlideUrl(
    val url: String,
) : GlideUrl(url, headers) {
    companion object {
        val headers: Headers = LazyHeaders.Builder()
            .addHeader("User-Agent", DOUBAN_USER_AGENT_STRING)
            .addHeader("Authorization", "Bearer null")
            .build()
    }
}