package com.doubean.ford.util;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.ShareCompat;

public class ShareUtil {
    public static void Share(Context context, String shareText) {
        Intent shareIntent = new ShareCompat.IntentBuilder(context)
                .setText(shareText)
                .setType("text/plain")
                .createChooserIntent();
        // https://android-developers.googleblog.com/2012/02/share-with-intents.html
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(shareIntent);
    }
}
