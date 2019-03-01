package com.example.sample.stockchart.data;

import android.util.SparseArray;

import com.example.sample.stockchart.model.TimeSharingDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingDataManage {
    private ArrayList<TimeSharingDataModel> mDatas = new ArrayList<>();//分时数据
    private double mBaseValue = 0;//分时图基准值
    private double mPermaxmin = 0;//分时图价格最大区间值
    private double mVolMaxTimeLine;//分时图最大成交量
    private double mMax = 0;//分时图最大价格
    private double mMin = 0;//分时图最小价格
    private double preClose;//昨收价
    private boolean mIsBSChart;

    /**
     * 外部传JSONObject解析获得分时数据集
     */
    public void parseTimeData(JSONObject object, double preClosePrice) {
        if (object != null) {
            mDatas.clear();

            preClose = object.optDouble("preClose", 0);
            final JSONArray data = object.optJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.length(); i++) {
                final TimeSharingDataModel dataModel = new TimeSharingDataModel();
                dataModel.setTimeMills(data.optJSONArray(i).optLong(0, 0L));
                dataModel.setNowPrice(data.optJSONArray(i).optDouble(1, 0));
                dataModel.setAveragePrice(data.optJSONArray(i).optDouble(2, 0));
                dataModel.setVolume(Double.valueOf(data.optJSONArray(i).optString(3, "0")).intValue());
                dataModel.setOpen(data.optJSONArray(i).optDouble(4, 0));
                dataModel.setPreClose(preClose != 0 ? preClose :
                        (preClosePrice == 0 ? dataModel.getOpen() : preClosePrice));

                if (i == 0) {
                    preClose = dataModel.getPreClose();
                    mMax = dataModel.getNowPrice();
                    mMin = dataModel.getNowPrice();
                    mVolMaxTimeLine = 0;
                    if (mBaseValue == 0) {
                        mBaseValue = dataModel.getPreClose();
                    }
                }

                mMax = Math.max(dataModel.getNowPrice(), mMax);
                mMin = Math.min(dataModel.getNowPrice(), mMin);

                mVolMaxTimeLine = Math.max(dataModel.getVolume(), mVolMaxTimeLine);
                mDatas.add(dataModel);
            }
            mPermaxmin = (mMax - mMin) / 2;
        }
    }

    //分时图左Y轴最大值
    public float getMax() {
        return (float) mMax;
    }

    //分时图左Y轴最小值
    public float getMin() {
        return (float) mMin;
    }

    //分时图右Y轴最大涨跌值
    public float getPercentMax() {
        return (float) (mPermaxmin / mBaseValue);
    }

    //分时图右Y轴最小涨跌值
    public float getPercentMin() {
        return -getPercentMax();
    }

    //分时图最大成交量
    public float getVolMaxTime() {
        return (float) mVolMaxTimeLine;
    }

    //分时图分钟数据集合
    public ArrayList<TimeSharingDataModel> getDatas() {
        return mDatas;
    }

    public boolean isBSChart() {
        return mIsBSChart;
    }

    public void setBSChart(boolean BSChart) {
        mIsBSChart = BSChart;
    }

    /**
     * 当日分时X轴刻度线
     */
    public SparseArray<String> getTimeSharingXLabels() {
        final SparseArray<String> xLabels = new SparseArray<>();
        xLabels.put(0, "09:30");
        xLabels.put(60, "10:30");
        xLabels.put(120, "11:30/13:00");
        xLabels.put(180, "14:00");
        xLabels.put(240, "15:00");
        return xLabels;
    }
}
