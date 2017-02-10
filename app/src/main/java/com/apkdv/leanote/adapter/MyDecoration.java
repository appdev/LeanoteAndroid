package com.apkdv.leanote.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.apkdv.leanote.utils.DvViewUtil;

/**
 * Created by LengYue on 2017/1/19.
 */

public class MyDecoration extends RecyclerView.ItemDecoration {

    private Paint dividerPaint;

    public MyDecoration(Context context) {
        dividerPaint = new Paint();
        dividerPaint.setColor(0xFFE4E8EA);

    }

    //在item下面划线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + DvViewUtil.dp2px(1);
            c.drawRect(left+DvViewUtil.dp2px(15), top, right-DvViewUtil.dp2px(15), bottom, dividerPaint);
        }
    }

    //由于划线 所以在item后面偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, DvViewUtil.dp2px(1));
    }
}
