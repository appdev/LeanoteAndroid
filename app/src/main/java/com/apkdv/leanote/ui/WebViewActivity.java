package com.apkdv.leanote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.apkdv.leanote.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LengYue on 2016/11/15.
 */

public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private String title;
    private String url;

    public final static String JUMP_URL = "jump_url";
    public final static String JUMP_STRING = "jump_string";


    public static void start(Context context, String url, @StringRes int title) {
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(JUMP_URL, url);
        starter.putExtra(JUMP_STRING, context.getString(title));
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        initView(getIntent());
        initWebView();
    }

    private void initView(Intent intent) {
        url = intent.getStringExtra(JUMP_URL);
        title = intent.getStringExtra(JUMP_STRING);
        initToolBar(mToolbar, true);
        getSupportActionBar().setTitle(title);
        webview.requestFocusFromTouch();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.toString());
                return true;
            }
        });


//        webview.getSettings().setUseWideViewPort(true);
//        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setWebChromeClient(new WebViewClient());
        webview.loadUrl(url);
    }

    private class WebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if (newProgress == 100) {
                pb.setVisibility(View.GONE);
                if (null != webview.getTitle()) {
                    title = webview.getTitle();
                }
            }
            super.onProgressChanged(view, newProgress);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
