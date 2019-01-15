package com.example.sample.stockchart.listener;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

/**
 * @author weixia
 * @date 2019/1/15.
 */
public class CoupleChartGestureListener implements OnChartGestureListener {
    private Chart srcChart;
    private Chart[] dstCharts;
    private CoupleClick coupleClick;

    public void setCoupleClick(CoupleClick coupleClick) {
        this.coupleClick = coupleClick;
    }

    public interface CoupleClick {
        void singleClickListener();
    }

    public CoupleChartGestureListener(Chart srcChart, Chart[] dstCharts) {
        this.srcChart = srcChart;
        this.dstCharts = dstCharts;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        syncCharts();
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if (dstCharts == null) {
            return;
        }
        syncCharts();
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            srcChart.highlightValue(null, true);
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        if (coupleClick != null) {
            coupleClick.singleClickListener();
        }
        syncCharts();
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        syncCharts();
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        syncCharts();
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        syncCharts();
    }

    private void syncCharts() {
        if (dstCharts == null) {
            return;
        }
        float[] srcVals = new float[9];
        float[] dstVals = new float[9];
        // get src chart translation matrix:
        final Matrix srcMatrix = srcChart.getViewPortHandler().getMatrixTouch();
        srcMatrix.getValues(srcVals);

        // apply X axis scaling and position to dst charts:
        for (Chart dstChart : dstCharts) {
            if (dstChart.getVisibility() == View.VISIBLE) {
                final Matrix dstMatrix = dstChart.getViewPortHandler().getMatrixTouch();
                dstMatrix.getValues(dstVals);

                dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
                dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
                dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
                dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
                dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
                dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
                dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
                dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
                dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];

                dstMatrix.setValues(dstVals);
                dstChart.getViewPortHandler().refresh(dstMatrix, dstChart, true);
            }
        }
    }
}
