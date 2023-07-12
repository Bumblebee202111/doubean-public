package com.doubean.ford.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.doubean.ford.model.GroupPostTag
import com.doubean.ford.util.DateTimeStyle
import com.doubean.ford.util.DoubanGlideUrl
import com.doubean.ford.util.dateTimeText
import java.time.LocalDateTime

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(DoubanGlideUrl(imageUrl))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("avatarFromUrl")
fun bindAvatarFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        val circleCrop = CircleCrop()
        Glide.with(view.context)
            .load(DoubanGlideUrl(imageUrl))
            .optionalTransform(circleCrop)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(circleCrop))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("groupPostTag", "groupPostTitle")
fun bindPostTagAndTitle(view: TextView, postTag: GroupPostTag?, postTitle: String?) {
    val sb = StringBuilder()
    if (postTag != null) sb.append("${postTag.name}｜")
    sb.append(postTitle)
    view.text = sb
}

@BindingAdapter("dateTime", "dateTimeStyle", requireAll = true)
fun bindDateTimeStringAndStyle(
    view: TextView,
    dateTime: LocalDateTime?,
    dateTimeStyle: DateTimeStyle,
) {
    if (dateTime != null) {
        view.text = dateTimeText(dateTime, dateTimeStyle)
    }
}