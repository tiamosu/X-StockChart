package com.example.sample.stockchart.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.sample.stockchart.TimeBarChartRenderer;
import com.example.sample.stockchart.TimeSharingXAxis;
import com.example.sample.stockchart.TimeSharingXAxisRenderer;
import com.example.sample.stockchart.TimeSharingYAxis;
import com.example.sample.stockchart.TimeSharingYAxisRenderer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;

/**
 * @author weixia
 * @date 2019/1/10.
 */
@SuppressWarnings("SuspiciousNameCombination")
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
        mRenderer = new TimeBarChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void initXAxisRenderer() {
        mXAxisRenderer = new TimeSharingXAxisRenderer(mViewPortHandler,
                (TimeSharingXAxis) mXAxis, mLeftAxisTransformer, this);
    }

    @Override
    protected void initYAxisRendererLeft() {
        mAxisRendererLeft = new TimeSharingYAxisRenderer(mViewPortHandler,
                (TimeSharingYAxis) mAxisLeft, mLeftAxisTransformer);
    }

    @Override
    protected void initYAxisRendererRight() {
        mAxisRendererRight = new TimeSharingYAxisRenderer(mViewPortHandler,
                (TimeSharingYAxis) mAxisRight, mRightAxisTransformer);
    }

    @Override
    public void initXAxis() {
        mXAxis = new TimeSharingXAxis();
    }

    @Override
    protected void initYAxisLeft() {
        mAxisLeft = new TimeSharingYAxis(YAxis.AxisDependency.LEFT);
    }

    @Override
    protected void initYAxisRight() {
        mAxisRight = new TimeSharingYAxis(YAxis.AxisDependency.RIGHT);
    }
}
