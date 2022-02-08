package com.doubean.ford.adapters;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class BindingAdapters {
    @BindingAdapter("isGone")
    public static void bindIsGone(View view, boolean isGone) {
        view.setVisibility(isGone ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("imageFromUrl")
    public static void bindImageFromUrl(ImageView view, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Transformation<Bitmap> circleCrop = new CircleCrop();
            Glide.with(view.getContext())
                    .load(imageUrl)
                    //.optionalTransform(circleCrop)
                    //.optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(circleCrop))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view);
        }
    }

    @BindingAdapter("date")
    public static void bindDate(TextView textView, Date date) {
        if (date != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String text;
            if (now.getYear() == localDateTime.getYear()) {
                if (now.getMonth() == localDateTime.getMonth()) {
                    if (now.getDayOfMonth() == localDateTime.getDayOfMonth()) {

                    } else {

                    }

                } else {

                }
            } else {

            }

            textView.setText(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault()).format(date));
        }
    }
}
