package com.example.sample.stockchart;

import com.github.mikephil.charting.components.YAxis;

/**
 * @author weixia
 * @date 2019/1/14.
 */
@SuppressWarnings("WeakerAccess")
public class TimeSharingYAxis extends YAxis {
    private int[] mLabelColorArray;

    public int[] getLabelColorArray() {
        return mLabelColorArray;
    }

    /**
     * 给每个label单独设置颜色
     */
    public void setLabelColorArray(int[] labelColorArray) {
        mLabelColorArray = labelColorArray;
    }

    public TimeSharingYAxis(AxisDependency position) {
        super(position);
    }
}
