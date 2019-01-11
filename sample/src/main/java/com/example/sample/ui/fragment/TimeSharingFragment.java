package com.example.sample.ui.fragment;

import android.view.View;

import com.example.sample.R;
import com.example.sample.base.BaseFragment;
import com.example.sample.common.data.ChartData;
import com.example.sample.stockchart.data.TimeSharingDataManage;
import com.example.sample.stockchart.view.TimeSharingChart;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * @author weixia
 * @date 2019/1/11.
 */
public class TimeSharingFragment extends BaseFragment {
    @BindView(R.id.time_sharing_chart)
    TimeSharingChart mChart;

    public static TimeSharingFragment newInstance() {
        return new TimeSharingFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_time_sharing;
    }

    @Override
    protected void onLoadData(View rootView) {
        mChart.initChart();
        //测试数据
        JSONObject object = null;
        try {
            object = new JSONObject(ChartData.TIMEDATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TimeSharingDataManage dataManage = new TimeSharingDataManage();
        dataManage.parseTimeData(object, 0);
        mChart.setDataToChart(dataManage);
    }
}
