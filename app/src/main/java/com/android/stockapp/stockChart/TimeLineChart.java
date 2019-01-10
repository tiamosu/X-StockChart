package com.android.stockapp.stockChart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.android.stockapp.stockChart.data.TimeDataManage;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

public class TimeLineChart extends LineChart {
    private LeftMarkerView myMarkerViewLeft;
    private TimeRightMarkerView myMarkerViewRight;
    private TimeDataManage kTimeData;
    private VolSelected volSelected;

    public void setVolSelected(VolSelected volSelected) {
        this.volSelected = volSelected;
    }

    public interface VolSelected {
        void onVolSelected(int value);

        void onValuesSelected(double price, double upDown, int vol, double avg);
    }

    public TimeLineChart(Context context) {
        super(context);
    }

    public TimeLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initRenderer() {
        mRenderer = new TimeLineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void initXAxisRenderer() {
        mXAxisRenderer = new TimeXAxisRenderer(mViewPortHandler, (TimeXAxis) mXAxis, mLeftAxisTransformer, this);
    }

    @Override
    public void initXAxis() {
        mXAxis = new TimeXAxis();
    }

    /*返回转型后的左右轴*/
    public void setMarker(LeftMarkerView markerLeft, TimeRightMarkerView markerRight, TimeDataManage kTimeData) {
        this.myMarkerViewLeft = markerLeft;
        this.myMarkerViewRight = markerRight;
        this.kTimeData = kTimeData;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        // if there is no marker view or drawing marker is disabled
        if (!isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (Highlight highlight : mIndicesToHighlight) {
            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(highlight);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX()) {
                continue;
            }

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1])) {
                continue;
            }

            float yValForXIndex1 = (float) kTimeData.getDatas().get((int) highlight.getX()).getNowPrice();
            float yValForXIndex2 = (float) kTimeData.getDatas().get((int) highlight.getX()).getPer();

            if (volSelected != null) {
                volSelected.onVolSelected(kTimeData.getDatas().get((int) highlight.getX()).getVolume());
                volSelected.onValuesSelected(kTimeData.getDatas().get((int) highlight.getX()).getNowPrice(),
                        kTimeData.getDatas().get((int) highlight.getX()).getPer(),
                        kTimeData.getDatas().get((int) highlight.getX()).getVolume(),
                        kTimeData.getDatas().get((int) highlight.getX()).getAveragePrice());
            }

            myMarkerViewLeft.setData(yValForXIndex1);
            myMarkerViewRight.setData(yValForXIndex2);

            myMarkerViewLeft.refreshContent(e, highlight);
            myMarkerViewRight.refreshContent(e, highlight);
            /*修复bug*/
            /*重新计算大小*/
            myMarkerViewLeft.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(), myMarkerViewLeft.getMeasuredHeight());
            myMarkerViewRight.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            myMarkerViewRight.layout(0, 0, myMarkerViewRight.getMeasuredWidth(), myMarkerViewRight.getMeasuredHeight());

            if (getAxisLeft().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft() - myMarkerViewLeft.getWidth() / 2, pos[1] + myMarkerViewLeft.getHeight() / 2);
            } else {
                myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft() + myMarkerViewLeft.getWidth() / 2, pos[1] + myMarkerViewLeft.getHeight() / 2);
            }
            if (getAxisRight().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                myMarkerViewRight.draw(canvas, mViewPortHandler.contentRight() + myMarkerViewRight.getWidth() / 2, pos[1] + myMarkerViewRight.getHeight() / 2);//- myMarkerViewRight.getWidth()
            } else {
                myMarkerViewRight.draw(canvas, mViewPortHandler.contentRight() - myMarkerViewRight.getWidth() / 2, pos[1] + myMarkerViewRight.getHeight() / 2);//- myMarkerViewRight.getWidth()
            }
        }
    }
}
