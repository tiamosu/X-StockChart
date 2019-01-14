package com.example.sample.stockchart;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;

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
