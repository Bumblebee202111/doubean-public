package com.doubean.ford.adapters;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class BindingAdapters {

    @BindingAdapter("isGone")
    public static void bindIsGone(View view, boolean isGone) {
        view.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("imageFromUrl")
    public static void bindImageFromUrl(ImageView view, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        }
    }

    @BindingAdapter("avatarFromUrl")
    public static void bindAvatarFromUrl(ImageView view, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Transformation<Bitmap> circleCrop = new CircleCrop();
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .optionalTransform(circleCrop)
                    .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(circleCrop))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        }
    }

    @BindingAdapter({"postTabName", "postTitle"})
    public static void bindPostTabNameAndTitle(TextView view, String postTabName, String postTitle) {
        String text = "";
        if (!TextUtils.isEmpty(postTabName))
            text += postTabName + "｜";
        text += postTitle;
        view.setText(text);
    }
}
