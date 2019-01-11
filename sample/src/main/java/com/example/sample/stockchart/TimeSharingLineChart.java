package com.example.sample.stockchart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingLineChart extends LineChart {

    public TimeSharingLineChart(Context context) {
        super(context);
    }

    public TimeSharingLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeSharingLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initRenderer() {
        mRenderer = new TimeSharingLineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void initXAxisRenderer() {
        mXAxisRenderer = new TimeSharingXAxisRenderer(mViewPortHandler, (TimeSharingXAxis) mXAxis, mLeftAxisTransformer, this);
    }

    @Override
    public void initXAxis() {
        mXAxis = new TimeSharingXAxis();
    }
}
