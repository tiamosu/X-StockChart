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

            preClose = Double.isNaN(object.optDouble("preClose")) ? 0 : object.optDouble("preClose");
            final JSONArray data = object.optJSONArray("data");
            if (data == null) {
                return;
            }
            for (int i = 0; i < data.length(); i++) {
                final TimeSharingDataModel timeSharingDataModel = new TimeSharingDataModel();
                timeSharingDataModel.setTimeMills(data.optJSONArray(i).optLong(0, 0L));
                timeSharingDataModel.setNowPrice(Double.isNaN(data.optJSONArray(i).optDouble(1)) ? 0 : data.optJSONArray(i).optDouble(1));
                timeSharingDataModel.setAveragePrice(Double.isNaN(data.optJSONArray(i).optDouble(2)) ? 0 : data.optJSONArray(i).optDouble(2));
                timeSharingDataModel.setVolume(Double.valueOf(data.optJSONArray(i).optString(3)).intValue());
                timeSharingDataModel.setOpen(Double.isNaN(data.optJSONArray(i).optDouble(4)) ? 0 : data.optJSONArray(i).optDouble(4));
                timeSharingDataModel.setPreClose(preClose == 0 ? (preClosePrice == 0 ? timeSharingDataModel.getOpen() : preClosePrice) : preClose);

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
        return (float) (mBaseValue + mBaseValue * getPercentMax());
    }

    //分时图左Y轴最小值
    public float getMin() {
        return (float) (mBaseValue + mBaseValue * getPercentMin());
    }

    //分时图右Y轴最大涨跌值
    public float getPercentMax() {
        //0.1表示Y轴最大涨跌值再增加10%，使图线不至于顶到最顶部
        return (float) ((mMax - mBaseValue) / mBaseValue + Math.abs(mMax - mBaseValue > mMin - mBaseValue ? mMax - mBaseValue : mMin - mBaseValue) / mBaseValue * 0.1);
    }

    //分时图右Y轴最小涨跌值
    public float getPercentMin() {
        //0.1表示Y轴最小涨跌值再减小10%，使图线不至于顶到最底部
        return (float) ((mMin - mBaseValue) / mBaseValue - Math.abs(mMax - mBaseValue > mMin - mBaseValue ? mMax - mBaseValue : mMin - mBaseValue) / mBaseValue * 0.1);
    }

    //分时图最大成交量
    public float getVolMaxTime() {
        return (float) mVolMaxTimeLine;
    }

    //分时图分钟数据集合
    public ArrayList<TimeSharingDataModel> getDatas() {
        return mRealTimeDataList;
    }

    public double getPreClose() {
        return preClose;
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
