package com.doubean.ford.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class OpenInUtil {
    public static void openInBrowser(Context context, String url) {
        Intent defaultBrowser = Intent
                .makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                .setData(Uri.parse(url));
        context.startActivity(defaultBrowser);
    }

    public static void openInDouban(Context context, String uri) {
        Intent doubanIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(doubanIntent);
    }
    //https://www.douban.com/doubanapp/dispatch?uri=/group/697689
}
