package com.apkdv.leanote.utils;


import android.content.Context;

import com.apkdv.leanote.R;
import com.apkdv.leanote.ui.WebViewActivity;

public class OpenUtils {

    public static void openUrl(Context context, String url) {
        try {
            WebViewActivity.start(context, url, R.string.retrieve_password);
        } catch (Exception ex) {
            ToastUtils.show(context, R.string.cant_open_url);
        }
    }
}
