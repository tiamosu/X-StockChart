package com.example.sample.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.example.sample.R;
import com.example.sample.base.BaseFragment;
import com.example.sample.common.data.ChartData;
import com.example.sample.common.data.Constants;
import com.example.sample.stockchart.data.KLineDataManage;
import com.example.sample.stockchart.view.KLineChart;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * @author weixia
 * @date 2019/1/14.
 */
public class ChartKLineFragment extends BaseFragment {
    @BindView(R.id.kline_chart)
    KLineChart mChart;

    private int mType;//日K：1；周K：7；月K：30
    private int mIndexType = 1;
    private KLineDataManage mDataManage;

    public static ChartKLineFragment newInstance(int type) {
        final ChartKLineFragment fragment = new ChartKLineFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(Constants.KEY_TYPE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_kline;
    }

    @Override
    protected void onLoadData(View rootView) {
        mDataManage = new KLineDataManage(getActivity());
        mChart.initChart();

        JSONObject object = null;
        try {
            if (mType == 1) {
                object = new JSONObject(ChartData.KLINEDATA);
            } else if (mType == 7) {
                object = new JSONObject(ChartData.KLINEWEEKDATA);
            } else if (mType == 30) {
                object = new JSONObject(ChartData.KLINEMONTHDATA);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //上证指数代码000001.IDX.SH
        mDataManage.parseKlineData(object);
        mChart.setDataToChart(mDataManage);

        mChart.gestureListenerBar.setCoupleClick(() -> loadIndexData(mIndexType < 5 ? ++mIndexType : 1));
    }

    private void loadIndexData(int type) {
        mIndexType = type;
        switch (type) {
            case 1://成交量
                mChart.doBarChartSwitch(type);
                break;
            case 2://请求MACD
                mDataManage.initMACD();
                mChart.doBarChartSwitch(type);
                break;
            case 3://请求KDJ
                mDataManage.initKDJ();
                mChart.doBarChartSwitch(type);
                break;
            case 4://请求BOLL
                mDataManage.initBOLL();
                mChart.doBarChartSwitch(type);
                break;
            case 5://请求RSI
                mDataManage.initRSI();
                mChart.doBarChartSwitch(type);
                break;
            default:
                break;
        }
    }
}
