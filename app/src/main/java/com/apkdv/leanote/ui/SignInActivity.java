package com.apkdv.leanote.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apkdv.leanote.R;
import com.apkdv.leanote.model.Authentication;
import com.apkdv.leanote.model.BaseResponse;
import com.apkdv.leanote.network.ApiProvider;
import com.apkdv.leanote.network.LeaFailure;
import com.apkdv.leanote.service.AccountService;
import com.apkdv.leanote.utils.OpenUtils;
import com.apkdv.leanote.utils.ToastUtils;
import com.apkdv.leanote.widget.OwlView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SignInActivity extends BaseActivity implements TextWatcher {

    private static final String TAG = "SignInActivity";

    private static final String LEANOTE_HOST = "https://leanote.com";
    private static final String FIND_PASSWORD = "/findPassword";
    private static final String EXT_IS_CUSTOM_HOST = "ext_is_custom_host";
    private static final String EXT_HOST = "ext_host";
    private static final String EXT_ACTION_PANEL_OFFSET_Y = "ext_host_et_height";
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private View.OnFocusChangeListener mFocusChangeListener;

    @BindView(R.id.owl_view)
    OwlView mOwlView;
    @BindView(R.id.et_email)
    EditText mEmailEt;
    @BindView(R.id.et_password)
    EditText mPasswordEt;
    @BindView(R.id.tv_sign_up)
    View mSignUpBtn;
    @BindView(R.id.tv_custom_host)
    TextView mCustomHostBtn;
    @BindView(R.id.et_custom_host)
    EditText mHostEt;
    @BindView(R.id.tv_example)
    TextView mEampleTv;
    //进度条
    ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        initView();
        boolean isCustomHost = false;
        String host = "";
        if (savedInstanceState != null) {
            isCustomHost = savedInstanceState.getBoolean(EXT_IS_CUSTOM_HOST);
            host = savedInstanceState.getString(EXT_HOST);
        }
        mCustomHostBtn.setTag(isCustomHost);
        mHostEt.setScaleY(isCustomHost ? 1 : 0);
        mHostEt.setText(host);
        mHostEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEampleTv.setText(String.format(Locale.US, "For example, login api will be:\n%s/api/login", s.toString()));
            }
        });
        mHostEt.setVisibility(View.GONE);
    }

    private void initView() {
        mFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if(v.getId() == R.id.password){
                if (hasFocus) {
                    mOwlView.open();
                    //Toast.makeText(MainActivity.this,"获取焦点",Toast.LENGTH_SHORT).show();
                } else {
                    mOwlView.close();
                    //Toast.makeText(MainActivity.this,"失去焦点",Toast.LENGTH_SHORT).show();
                }
            }
        };
        mPasswordEt.setOnFocusChangeListener(mFocusChangeListener);
        mEmailEt.addTextChangedListener(this);
        mPasswordEt.addTextChangedListener(this);
        mHostEt.setPivotY(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXT_IS_CUSTOM_HOST, (Boolean) mCustomHostBtn.getTag());
        outState.putString(EXT_HOST, mHostEt.getText().toString());
    }

    @OnClick(R.id.tv_forgot_password)
    void clickedForgotPassword() {
        String url = getHost() + FIND_PASSWORD;
        OpenUtils.openUrl(this, url);
    }

    @OnClick(R.id.tv_custom_host)
    void switchHost() {
        boolean isCustomHost = !(boolean) mCustomHostBtn.getTag();
        mCustomHostBtn.setTag(isCustomHost);
        mEampleTv.setVisibility(isCustomHost ? View.VISIBLE : View.GONE);
        if (isCustomHost) {
            mHostEt.setVisibility(View.VISIBLE);
            mCustomHostBtn.setText(R.string.use_leanote_host);
            mHostEt.animate()
                    .scaleY(1)
                    .setDuration(200)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

        } else {
            mHostEt.setVisibility(View.GONE);
            mCustomHostBtn.setText(R.string.use_custom_host);
            mHostEt.animate()
                    .scaleY(0)
                    .setDuration(200)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private String getHost() {
        return (boolean) mCustomHostBtn.getTag() ? mHostEt.getText().toString().trim() : LEANOTE_HOST;
    }

    @OnClick(R.id.btn_login)
    void signIn() {
        final String email = mEmailEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        final String host = getHost();
        initHost()
                .flatMap(new Func1<String, Observable<Authentication>>() {
                    @Override
                    public Observable<Authentication> call(String s) {
                        return AccountService.login(email, password);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mBtnLogin.post(new Runnable() {
                            @Override
                            public void run() {
                                createProgress("登录中...");
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Authentication>() {
                    @Override
                    public void onCompleted() {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof IllegalHostException) {
                            ToastUtils.show(SignInActivity.this, R.string.illegal_host);
                        } else {
                            ToastUtils.showNetworkError(SignInActivity.this);
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(Authentication authentication) {
                        if (authentication.isOk()) {
                            AccountService.saveToAccount(authentication, host);
                            Intent intent = MainActivity2.getOpenIntent(SignInActivity.this, true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtils.show(SignInActivity.this, R.string.email_or_password_incorrect);
                        }
                    }
                });
    }

    @OnClick(R.id.tv_sign_up)
    void clickedSignUp() {
        final String email = mEmailEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        final String host = getHost();
        initHost()
                .flatMap(new Func1<String, Observable<BaseResponse>>() {
                    @Override
                    public Observable<BaseResponse> call(String s) {
                        return AccountService.register(email, password);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mSignUpBtn.post(new Runnable() {
                            @Override
                            public void run() {
                                createProgress("注册中...");
                            }
                        });
                    }
                })
                .flatMap(new Func1<BaseResponse, Observable<Authentication>>() {
                    @Override
                    public Observable<Authentication> call(BaseResponse baseResponse) {
                        if (baseResponse.isOk()) {
                            return AccountService.login(email, password);
                        } else {
                            throw new LeaFailure(baseResponse);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Authentication>() {
                    @Override
                    public void onCompleted() {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof IllegalHostException) {
                            ToastUtils.show(SignInActivity.this, R.string.illegal_host);
                        } else {
                            ToastUtils.showNetworkError(SignInActivity.this);
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(Authentication authentication) {
                        if (authentication.isOk()) {
                            AccountService.saveToAccount(authentication, host);
                            Intent intent = MainActivity.getOpenIntent(SignInActivity.this, true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtils.show(SignInActivity.this, R.string.email_or_password_incorrect);
                        }
                    }
                });
    }

    private Observable<String> initHost() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    String host = getHost();
                    if (host.matches("^(http|https)://[^\\s]+")) {
                        ApiProvider.getInstance().init(host);
                        subscriber.onNext(host);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new IllegalHostException());
                    }
                }
            }
        });
    }

    private static class IllegalHostException extends Exception {
    }


    private void createProgress(String msg) {
        if (mDialog == null)
            mDialog = ProgressDialog.show(this, "", msg);
        else
            mDialog.setMessage(msg);
    }


    @Override

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String email = mEmailEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        mBtnLogin.setEnabled(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password));
    }
}
