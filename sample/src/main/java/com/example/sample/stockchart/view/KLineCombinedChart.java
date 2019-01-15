package com.example.sample.stockchart.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.sample.stockchart.KLineCombinedChartRenderer;
import com.github.mikephil.charting.charts.CombinedChart;

/**
 * @author weixia
 * @date 2019/1/15.
 */
public class KLineCombinedChart extends CombinedChart {

    public KLineCombinedChart(Context context) {
        super(context);
    }

    public KLineCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KLineCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initRenderer() {
        mRenderer = new KLineCombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }
}
