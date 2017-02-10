/****************************************************************************
 * Copyright (C) 2014 www.apkdv.com
 * Author: LengYue  ProjectName: DvTools
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/
package com.apkdv.leanote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences的一个工具类，调用setParam就能保存String, Integer, Boolean, Float, Long类型的参数 同样调用getParam就能获取到保存在手机里面的数据
 *
 * @author xiaanming
 */
public class DvSharedPreferences {
    private static SharedPreferences mSharedPreferences;

    public DvSharedPreferences() {
    }

    public static void init(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public static void setString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defValue) {
        SharedPreferences sharedPref = mSharedPreferences;
        return sharedPref.getInt(key, defValue);
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defValue) {
        SharedPreferences sharedPref = mSharedPreferences;
        return sharedPref.getLong(key, defValue);
    }

    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static float getFloat(String key, float defValue) {
        SharedPreferences sharedPref = mSharedPreferences;
        return sharedPref.getFloat(key, defValue);
    }

    public static void setFloat(String key, float value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sharedPref = mSharedPreferences;
        return sharedPref.getBoolean(key, defValue);
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
