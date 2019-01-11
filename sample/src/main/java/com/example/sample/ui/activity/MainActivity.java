package com.example.sample.ui.activity;

import android.content.Intent;

import com.example.sample.MyApp;
import com.example.sample.R;
import com.example.sample.base.BaseActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MyApp.getApplication().initDayNight();
    }

    @OnClick(R.id.btn_chart_test)
    public void onViewClicked() {
        startActivity(new Intent(this, StockDetailActivity.class));
    }
}
