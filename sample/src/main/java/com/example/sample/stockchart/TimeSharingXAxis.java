package com.example.sample.stockchart;

import android.util.SparseArray;

import com.github.mikephil.charting.components.XAxis;

/**
 * @author weixia
 * @date 2019/1/10.
 */
@SuppressWarnings("WeakerAccess")
public class TimeSharingXAxis extends XAxis {
    private SparseArray<String> mLabels;

    public SparseArray<String> getXLabels() {
        return mLabels;
    }

    public void setXLabels(SparseArray<String> labels) {
        this.mLabels = labels;
    }
}
