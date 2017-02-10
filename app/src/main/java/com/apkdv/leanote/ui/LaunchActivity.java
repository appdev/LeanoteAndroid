package com.apkdv.leanote.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.apkdv.leanote.model.Account;
import com.apkdv.leanote.network.ApiProvider;
import com.apkdv.leanote.service.AccountService;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (AccountService.isSignedIn()) {
            Account account = AccountService.getCurrent();
            ApiProvider.getInstance().init(account.getHost());
            intent = MainActivity2.getOpenIntent(this, false);
        } else {
            intent = new Intent(this, SignInActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
