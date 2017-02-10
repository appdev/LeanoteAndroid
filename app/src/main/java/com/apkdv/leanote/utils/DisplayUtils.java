package com.apkdv.leanote.utils;


import com.apkdv.leanote.Leamonax;

public class DisplayUtils {
    public static int dp2px(float dp) {
        final float scale = Leamonax.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(float px) {
        final float scale = Leamonax.getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
