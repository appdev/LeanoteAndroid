package com.apkdv.leanote.service;

import com.apkdv.leanote.database.AppDataBase;
import com.apkdv.leanote.model.Account;
import com.apkdv.leanote.model.Authentication;
import com.apkdv.leanote.model.BaseResponse;
import com.apkdv.leanote.model.User;
import com.apkdv.leanote.network.ApiProvider;
import com.apkdv.leanote.utils.RetrofitUtils;

import rx.Observable;

public class AccountService {

    public static Observable<BaseResponse> register(String email, String password) {
        return RetrofitUtils.create(ApiProvider.getInstance().getAuthApi().register(email, password));
    }

    public static Observable<Authentication> login(String email, String password) {
        return RetrofitUtils.create(ApiProvider.getInstance().getAuthApi().login(email, password));
    }

    public static Observable<User> getInfo(String userId) {
        return RetrofitUtils.create(ApiProvider.getInstance().getUserApi().getInfo(userId));
    }

    public static void saveToAccount(Authentication authentication, String host) {
        Account localAccount = AppDataBase.getAccount(authentication.getEmail(), host);
        if (localAccount == null) {
            localAccount = new Account();
        }
        localAccount.setHost(host);
        localAccount.setEmail(authentication.getEmail());
        localAccount.setAccessToken(authentication.getAccessToken());
        localAccount.setUserId(authentication.getUserId());
        localAccount.setUserName(authentication.getUserName());
        localAccount.save();
    }

    public static void saveToAccount(User user, String host) {
        Account localAccount = AppDataBase.getAccount(user.getEmail(), host);
        if (localAccount == null) {
            localAccount = new Account();
        }
        localAccount.setHost(host);
        localAccount.setEmail(user.getEmail());
        localAccount.setUserId(user.getUserId());
        localAccount.setUserName(user.getUserName());
        localAccount.setAvatar(user.getAvatar());
        localAccount.setVerified(user.isVerified());
        localAccount.save();
    }

    public static void logout() {
        Account account = getCurrent();
        account.setAccessToken("");
        account.update();
    }

    public static Observable<BaseResponse> changePassword(String oldPassword, String newPassword) {
        return RetrofitUtils.create(ApiProvider.getInstance().getUserApi().updatePassword(oldPassword, newPassword));
    }

    public static Observable<BaseResponse> changeUserName(String userName) {
        return RetrofitUtils.create(ApiProvider.getInstance().getUserApi().updateUsername(userName));
    }

    public static Account getCurrent() {
        return AppDataBase.getAccountWithToken();
    }

    public static boolean isSignedIn() {
        return getCurrent() != null;
    }
}
