package com.example.sample.stockchart.listener;

import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

/**
 * @author weixia
 * @date 2019/1/15.
 */
public class CoupleChartGestureListener implements OnChartGestureListener {
    private BarLineChartBase mSrcChart;
    private Chart[] mDstCharts;
    private CoupleClick mCoupleClick;
    private OnEdgeListener mOnEdgeListener;
    private boolean mCanLoad;//K线图手指交互已停止，正在惯性滑动

    public void setCoupleClick(CoupleClick coupleClick) {
        this.mCoupleClick = coupleClick;
    }

    public void setOnEdgeListener(OnEdgeListener onEdgeListener) {
        mOnEdgeListener = onEdgeListener;
    }

    public CoupleChartGestureListener(BarLineChartBase srcChart, Chart[] dstCharts) {
        this.mSrcChart = srcChart;
        this.mDstCharts = dstCharts;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        mCanLoad = false;
        syncCharts();
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if (mSrcChart == null) {
            return;
        }
        final float leftX = mSrcChart.getLowestVisibleX();
        final float rightX = mSrcChart.getHighestVisibleX();
        final float minVisible = 10f;
        if (leftX <= mSrcChart.getXChartMin() + minVisible) {//滑到最左端的minVisible范围内
            mCanLoad = false;
            if (mOnEdgeListener != null) {
                mOnEdgeListener.edgeLoad(leftX, true);
            }
        } else if (rightX >= mSrcChart.getXChartMax() - minVisible) {//滑到最右端的minVisible范围内
            mCanLoad = false;
            if (mOnEdgeListener != null) {
                mOnEdgeListener.edgeLoad(rightX, false);
            }
        } else {
            mCanLoad = true;
        }

        syncCharts();
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            mSrcChart.highlightValue(null, true);
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
        if (mCoupleClick != null) {
            mCoupleClick.singleClickListener();
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
        if (mCanLoad) {
            final float leftX = mSrcChart.getLowestVisibleX();
            final float rightX = mSrcChart.getHighestVisibleX();
            final float minVisible = 10f;
            if (leftX <= mSrcChart.getXChartMin() + minVisible) {//滑到最左端的minVisible范围内
                mCanLoad = false;
                if (mOnEdgeListener != null) {
                    mOnEdgeListener.edgeLoad(leftX, true);
                }
            } else if (rightX >= mSrcChart.getXChartMax() - minVisible) {//滑到最右端的minVisible范围内
                mCanLoad = false;
                if (mOnEdgeListener != null) {
                    mOnEdgeListener.edgeLoad(rightX, false);
                }
            }
        }

        syncCharts();
    }

    private void syncCharts() {
        if (mDstCharts == null || mSrcChart == null) {
            return;
        }
        float[] srcVals = new float[9];
        float[] dstVals = new float[9];
        // get src chart translation matrix:
        final Matrix srcMatrix = mSrcChart.getViewPortHandler().getMatrixTouch();
        srcMatrix.getValues(srcVals);

        // apply X axis scaling and position to dst charts:
        for (Chart dstChart : mDstCharts) {
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

    public interface CoupleClick {
        void singleClickListener();
    }

    public interface OnEdgeListener {
        void edgeLoad(float x, boolean left);
    }
}
