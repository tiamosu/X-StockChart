package com.example.sample;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sample.common.data.Constants;
import com.example.sample.base.BaseApp;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class MyApp extends BaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
        initDayNight();
    }

    public static MyApp getApplication() {
        return (MyApp) getApp();
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
