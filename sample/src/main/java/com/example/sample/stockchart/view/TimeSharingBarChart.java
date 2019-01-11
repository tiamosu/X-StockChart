package com.example.sample.stockchart.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.sample.stockchart.TimeSharingBarChartRenderer;
import com.example.sample.stockchart.TimeSharingXAxis;
import com.example.sample.stockchart.TimeSharingXAxisRenderer;
import com.github.mikephil.charting.charts.BarChart;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingBarChart extends BarChart {

    public TimeSharingBarChart(Context context) {
        super(context);
    }

    public TimeSharingBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeSharingBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initMyBarRenderer() {
        mRenderer = new TimeSharingBarChartRenderer(this, mAnimator, mViewPortHandler);
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
