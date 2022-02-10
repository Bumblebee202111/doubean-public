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

    @BindingAdapter({"date", "tagName"})
    public static void bindDateAndTopicName(TextView textView, Date date, String tagName) {
        if (date != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String dateTimeText;
            if (now.getYear() == localDateTime.getYear() && now.getDayOfYear() == localDateTime.getDayOfYear()) {
                dateTimeText = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
            } else {
                dateTimeText = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
            }
            if (TextUtils.isEmpty(tagName)) {
                textView.setText(dateTimeText);
            } else {
                textView.setText(tagName + " · " + dateTimeText);
            }
        }
    }

    @BindingAdapter("date")
    public static void bindDate(TextView textView, Date date) {
        if (date != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String text;
            if (now.getYear() == localDateTime.getYear() && now.getDayOfYear() == localDateTime.getDayOfYear()) {
                text = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
            } else {
                text = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
            }

            textView.setText(text);
        }
    }
}
