package com.github.mikephil.charting.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by philipp on 12/06/15.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ChartTouchListener<T extends Chart<?>> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    public enum ChartGesture {
        NONE, DRAG, X_ZOOM, Y_ZOOM, PINCH_ZOOM, ROTATE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
    }

    /**
     * the last touch gesture that has been performed
     **/
    protected ChartGesture mLastGesture = ChartGesture.NONE;

    // states
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int X_ZOOM = 2;
    protected static final int Y_ZOOM = 3;
    protected static final int PINCH_ZOOM = 4;
    protected static final int POST_ZOOM = 5;
    protected static final int ROTATE = 6;

    /**
     * integer field that holds the current touch-state
     */
    protected int mTouchMode = NONE;

    /**
     * the last highlighted object (via touch)
     */
    protected Highlight mLastHighlighted;

    /**
     * the gesturedetector used for detecting taps and longpresses, ...
     */
    protected GestureDetector mGestureDetector;

    /**
     * the chart the listener represents
     */
    protected T mChart;

    public ChartTouchListener(T chart) {
        this.mChart = chart;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    /**
     * Calls the OnChartGestureListener to do the start callback
     */
    public void startAction(MotionEvent me) {
        final OnChartGestureListener l = mChart.getOnChartGestureListener();
        if (l != null) {
            l.onChartGestureStart(me, mLastGesture);
        }
    }

    /**
     * Calls the OnChartGestureListener to do the end callback
     */
    public void endAction(MotionEvent me) {
        final OnChartGestureListener l = mChart.getOnChartGestureListener();
        if (l != null) {
            l.onChartGestureEnd(me, mLastGesture);
        }
    }

    /**
     * Sets the last value that was highlighted via touch.
     */
    public void setLastHighlighted(Highlight high) {
        mLastHighlighted = high;
    }

    /**
     * returns the touch mode the listener is currently in
     */
    public int getTouchMode() {
        return mTouchMode;
    }

    /**
     * Returns the last gesture that has been performed on the chart.
     */
    public ChartGesture getLastGesture() {
        return mLastGesture;
    }

    /**
     * Perform a highlight operation.
     */
    protected void performHighlight(Highlight h, MotionEvent e) {
        if (h == null || h.equalTo(mLastHighlighted)) {
            mChart.highlightValue(null, true);
            mLastHighlighted = null;
        } else {
            mChart.highlightValue(h, true);
            mLastHighlighted = h;
        }
    }

    /**
     * returns the distance between two points
     */
    protected static float distance(float eventX, float startX, float eventY, float startY) {
        final float dx = eventX - startX;
        final float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
