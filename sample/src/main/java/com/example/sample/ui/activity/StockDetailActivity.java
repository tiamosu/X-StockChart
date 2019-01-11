package com.example.sample.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.sample.R;
import com.example.sample.base.BaseActivity;
import com.example.sample.common.adapter.SimpleFragmentPagerAdapter;
import com.example.sample.common.data.Constants;
import com.example.sample.common.viewpager.NoTouchScrollViewpager;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;

/**
 * @author weixia
 * @date 2019/1/11.
 */
public class StockDetailActivity extends BaseActivity {
    @BindView(R.id.stock_detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.stock_detail_tab)
    TabLayout mTabLayout;
    @BindView(R.id.stock_detail_view_pager)
    NoTouchScrollViewpager mViewpager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_stock_detail;
    }

    @Override
    protected void onLoadData() {
        mToolbar.setTitle("图表");
        mToolbar.inflateMenu(R.menu.menu_right);
        mToolbar.setNavigationOnClickListener(view -> finish());
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_model:
                    final SharedPreferences sp = getSharedPreferences(Constants.SP_FILE, Context.MODE_PRIVATE);
                    if (!sp.getBoolean(Constants.DAY_NIGHT_MODE, false)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        sp.edit().putBoolean(Constants.DAY_NIGHT_MODE, true).apply();
                        Toast.makeText(StockDetailActivity.this, "夜间模式!", Toast.LENGTH_SHORT).show();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        sp.edit().putBoolean(Constants.DAY_NIGHT_MODE, false).apply();
                        Toast.makeText(StockDetailActivity.this, "白天模式!", Toast.LENGTH_SHORT).show();
                    }
                    recreate();
                    break;
                default:
                    break;
            }
            return true;
        });

        final Fragment[] fragments = {

        };
        final String[] titles = {

        };
        mViewpager.setOffscreenPageLimit(fragments.length);
        mViewpager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setupWithViewPager(mViewpager);
    }
}
