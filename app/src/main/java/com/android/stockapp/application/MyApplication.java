package com.android.stockapp.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.stockapp.common.data.Constants;
import com.android.stockapp.ui.base.BaseApp;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
        initDayNight();
    }

    public static MyApplication getApplication() {
        return (MyApplication) getApp();
    }

    public void initDayNight() {
        //初始化夜间模式
        final SharedPreferences sp = getSharedPreferences(Constants.SP_FILE, Context.MODE_PRIVATE);
        if (sp.getBoolean(Constants.DAY_NIGHT_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
