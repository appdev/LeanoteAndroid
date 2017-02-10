package com.apkdv.leanote.utils;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apkdv.leanote.Constant;
import com.apkdv.leanote.R;
import com.apkdv.leanote.widget.guideview.Component;

/**
 * Created by LengYue on 2017/2/9.
 */

public class SimpleComponent implements Component {
    String type;
    private ImageView imagearrow;
    private TextView textinfo;
    private RelativeLayout mLayout;

    public SimpleComponent(String type) {
        this.type = type;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        mLayout = (RelativeLayout) inflater.inflate(R.layout.layer_frends, null);
        this.textinfo = (TextView) mLayout.findViewById(R.id.text_info);
        this.imagearrow = (ImageView) mLayout.findViewById(R.id.image_arrow);
        switch (type) {
            case Constant.HOME_INDEX:
                ObjectAnimator.ofFloat(imagearrow, "rotation", 0f, -10f).setDuration(1).start();
                textinfo.setText("点击头像，进入个人设置界面。");
                break;
        }
        return mLayout;
    }

    @Override
    public int getAnchor() {
        switch (type) {
            case Constant.HOME_INDEX:
                return Component.ANCHOR_BOTTOM;
            default:
                return 0;
        }
    }

    @Override
    public int getFitPosition() {
        switch (type) {
            case Constant.HOME_INDEX:
                return Component.FIT_START;

            default:
                return 0;
        }

    }

    @Override
    public int getXOffset() {
        return 35;
    }

    @Override
    public int getYOffset() {
        return -3;
    }
}
