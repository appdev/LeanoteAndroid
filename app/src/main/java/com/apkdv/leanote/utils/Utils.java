package com.apkdv.leanote.utils;

import android.app.Activity;
import android.view.View;


import com.apkdv.leanote.Constant;
import com.apkdv.leanote.widget.guideview.Component;
import com.apkdv.leanote.widget.guideview.Guide;
import com.apkdv.leanote.widget.guideview.GuideBuilder;

/**
 * Created by LengYue on 2017/2/9.
 */

public class Utils {

    public static Guide showGuideView(View targetView, final Activity activity, final String textString) {
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(targetView)
//                .setFullingViewId(id)
                .setAlpha(150)
                .setAutoDismiss(true)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        switch (textString) {
            case Constant.HOME_INDEX:
                builder.setHighTargetPadding(0)
                        .setHighTargetGraphStyle(Component.CIRCLE);
                break;
        }
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                switch (textString) {
                    case Constant.HOME_INDEX:
                        DvSharedPreferences.setBoolean(Constant.HOME_INDEX, true);
                        break;
                }

            }
        });

        builder.addComponent(new SimpleComponent(textString));
        Guide guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(activity);
        return guide;
    }
}
