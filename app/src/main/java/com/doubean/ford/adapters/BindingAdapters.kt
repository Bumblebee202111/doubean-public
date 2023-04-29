package com.doubean.ford.adapters

import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("isGone")
    fun bindIsGone(view: View, isGone: Boolean) {
        view.visibility = if (isGone) View.GONE else View.VISIBLE
    }
    @JvmStatic
    @BindingAdapter("imageFromUrl")
    fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(view.context)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
        }
    }
    @JvmStatic
    @BindingAdapter("avatarFromUrl")
    fun bindAvatarFromUrl(view: ImageView, imageUrl: String?) {
        if (!TextUtils.isEmpty(imageUrl)) {
            val circleCrop: Transformation<Bitmap> = CircleCrop()
            Glide.with(view.context)
                    .load(imageUrl)
                    .optionalTransform(circleCrop)
                    .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(circleCrop))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
        }
    }
    @JvmStatic
    @BindingAdapter("postTabName", "postTitle")
    fun bindPostTabNameAndTitle(view: TextView, postTabName: String?, postTitle: String?) {
        var text: String? = ""
        if (!TextUtils.isEmpty(postTabName)) text += "$postTabName｜"
        text += postTitle
        view.text = text
    }
}