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
    private ArrayList<TimeSharingDataModel> mRealTimeDataList = new ArrayList<>();//分时数据
    private double mBaseValue = 0;//分时图基准值
    private double mPermaxmin = 0;//分时图价格最大区间值
    private int mAllVolume = 0;//分时图总成交量
    private double mVolMaxTimeLine;//分时图最大成交量
    private double mMax = 0;//分时图最大价格
    private double mMin = 0;//分时图最小价格
    private double mPerVolMaxTimeLine = 0;
    private double preClose;//昨收价

    /**
     * 外部传JSONObject解析获得分时数据集
     */
    public void parseTimeData(JSONObject object, double preClosePrice) {
        if (object != null) {
            mRealTimeDataList.clear();

            preClose = object.optDouble("preClose", 0);
            final JSONArray data = object.optJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.length(); i++) {
                final TimeSharingDataModel timeSharingDataModel = new TimeSharingDataModel();
                timeSharingDataModel.setTimeMills(data.optJSONArray(i).optLong(0, 0L));
                timeSharingDataModel.setNowPrice(data.optJSONArray(i).optDouble(1, 0));
                timeSharingDataModel.setAveragePrice(data.optJSONArray(i).optDouble(2, 0));
                timeSharingDataModel.setVolume(Double.valueOf(data.optJSONArray(i).optString(3, "0")).intValue());
                timeSharingDataModel.setOpen(data.optJSONArray(i).optDouble(4, 0));
                timeSharingDataModel.setPreClose(preClose != 0 ? preClose :
                        (preClosePrice == 0 ? timeSharingDataModel.getOpen() : preClosePrice));

                if (i == 0) {
                    preClose = timeSharingDataModel.getPreClose();
                    mAllVolume = timeSharingDataModel.getVolume();
                    mMax = timeSharingDataModel.getNowPrice();
                    mMin = timeSharingDataModel.getNowPrice();
                    mVolMaxTimeLine = 0;
                    if (mBaseValue == 0) {
                        mBaseValue = timeSharingDataModel.getPreClose();
                    }
                } else {
                    mAllVolume += timeSharingDataModel.getVolume();
                }
                timeSharingDataModel.setCha(timeSharingDataModel.getNowPrice() - preClose);
                timeSharingDataModel.setPer(timeSharingDataModel.getCha() / preClose);

                mMax = Math.max(timeSharingDataModel.getNowPrice(), mMax);
                mMin = Math.min(timeSharingDataModel.getNowPrice(), mMin);

                mPerVolMaxTimeLine = mVolMaxTimeLine;
                mVolMaxTimeLine = Math.max(timeSharingDataModel.getVolume(), mVolMaxTimeLine);
                mRealTimeDataList.add(timeSharingDataModel);
            }
            mPermaxmin = (mMax - mMin) / 2;
        }
    }

    public void removeLastData() {
        final TimeSharingDataModel realTimeData = getRealTimeData().get(getRealTimeData().size() - 1);
        mAllVolume -= realTimeData.getVolume();
        mVolMaxTimeLine = mPerVolMaxTimeLine;
        getRealTimeData().remove(getRealTimeData().size() - 1);
    }

    private synchronized ArrayList<TimeSharingDataModel> getRealTimeData() {
        return mRealTimeDataList;
    }

    public void resetTimeData() {
        mBaseValue = 0;
        getRealTimeData().clear();
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
        return mRealTimeDataList;
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
