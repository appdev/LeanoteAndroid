package com.apkdv.leanote.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.apkdv.leanote.Leamonax;
import com.apkdv.leanote.R;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.toolbar));
        }
        ((Leamonax) getApplication()).mActivities.add(this);

    }

    protected void initToolBar(Toolbar toolbar) {
        initToolBar(toolbar, false);
    }

    protected void initToolBar(Toolbar toolbar, boolean hasBackArrow) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(0xffFAFAFA);
            toolbar.setTitle(getTitle());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && hasBackArrow) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 键盘自动隐藏
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            ArrayList<View> ignoreViewList = getIgnoreView();
            boolean shouldHide = true;
            int size = ignoreViewList.size();
            for (int i = 0; i < size; i++) {
                boolean hide = isShouldHideInput(ignoreViewList.get(i), ev);
                if (!hide) {
                    shouldHide = false;
                    break;
                }
            }

            if (isShouldHideInput(v, ev) && shouldHide) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private ArrayList<View> mIgnoreList = new ArrayList<>();

    private ArrayList<View> getIgnoreView() {
        return mIgnoreList;
    }

    //设置View点击时，不收缩键盘
    public void addIgnoreView(View view) {
        if (null != view) {
            mIgnoreList.add(view);
        } else {
            mIgnoreList.clear();
        }
    }


    /**
     * 判断是否为可输入或可点击View
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && !(v instanceof EditText || v instanceof Button)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
