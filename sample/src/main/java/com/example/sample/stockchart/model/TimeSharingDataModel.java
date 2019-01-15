package com.example.sample.stockchart.model;

import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingDataModel implements Serializable {
    //时间戳
    private Long timeMills = 0L;
    //现价
    private double nowPrice;
    //均价
    private double averagePrice;
    //分钟成交量
    private int volume;
    //今开
    private double open;
    //昨收
    private double preClose;
    private int color = 0xff000000;

    public Long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(Long timeMills) {
        this.timeMills = timeMills;
    }

    public double getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(double nowPrice) {
        this.nowPrice = nowPrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getPreClose() {
        return preClose;
    }

    public void setPreClose(double preClose) {
        this.preClose = preClose;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof TimeSharingDataModel) {
            final TimeSharingDataModel model = (TimeSharingDataModel) obj;
            return getTimeMills().equals(model.getTimeMills());
        }
        return super.equals(obj);
    }
}
