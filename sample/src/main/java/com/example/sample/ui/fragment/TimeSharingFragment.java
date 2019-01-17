package com.example.sample.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.sample.R;
import com.example.sample.base.BaseFragment;
import com.example.sample.common.data.ChartData;
import com.example.sample.common.data.Constants;
import com.example.sample.stockchart.data.TimeSharingDataManage;
import com.example.sample.stockchart.view.TimeSharingChart;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * @author weixia
 * @date 2019/1/11.
 */
public class TimeSharingFragment extends BaseFragment {
    @BindView(R.id.time_sharing_chart)
    TimeSharingChart mChart;

    private boolean mIsBSChart;
    private static final Handler HANDLER = new Handler();

    public static TimeSharingFragment newInstance(boolean isBSChart) {
        final TimeSharingFragment fragment = new TimeSharingFragment();
        final Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_KEY, isBSChart);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsBSChart = getArguments().getBoolean(Constants.BUNDLE_KEY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_time_sharing;
    }

    @Override
    protected void onLoadData(View rootView) {
        mChart.initChart();

        HANDLER.postDelayed(() -> {
            //测试数据
            JSONObject object = null;
            try {
                object = new JSONObject(ChartData.TIMEDATA);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final TimeSharingDataManage dataManage = new TimeSharingDataManage();
            dataManage.parseTimeData(object, 0);
            dataManage.setBSChart(mIsBSChart);
            mChart.setDataToChart(dataManage);
        }, 3000);
    }

    @Override
    public void onDestroy() {
        HANDLER.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
