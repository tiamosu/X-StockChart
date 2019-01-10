package com.android.stockapp.stockChart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.android.stockapp.stockChart.data.TimeDataManage;
import com.github.mikephil.charting.utils.DataTimeUtil;

public class TimeBarChart extends BarChart {
    private BarBottomMarkerView markerBottom;
    private TimeDataManage kTimeData;

    public TimeBarChart(Context context) {
        super(context);
    }

    public TimeBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMarker(BarBottomMarkerView markerBottom, TimeDataManage kTimeData) {
        this.markerBottom = markerBottom;
        this.kTimeData = kTimeData;
    }

    @Override
    public void initMyBarRenderer() {//调用自己的渲染器
        mRenderer = new TimeBarChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void initXAxisRenderer() {
        mXAxisRenderer = new TimeXAxisRenderer(mViewPortHandler, (TimeXAxis) mXAxis, mLeftAxisTransformer, this);
    }

    @Override
    public void initXAxis() {
        mXAxis = new TimeXAxis();
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

            String date = "";

            date = DataTimeUtil.secToTime(kTimeData.getDatas().get((int) e.getX()).getTimeMills());//分时图显示的数据

            markerBottom.setData(date);
            markerBottom.refreshContent(e, highlight);
            markerBottom.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            markerBottom.layout(0, 0, markerBottom.getMeasuredWidth(), markerBottom.getMeasuredHeight());

            int width = markerBottom.getWidth() / 2;
            if (mViewPortHandler.contentRight() - pos[0] <= width) {
                markerBottom.draw(canvas, mViewPortHandler.contentRight() - markerBottom.getWidth() / 2, mViewPortHandler.contentBottom() + markerBottom.getHeight());//-markerBottom.getHeight()   CommonUtil.dip2px(getContext(),65.8f)
            } else if (pos[0] - mViewPortHandler.contentLeft() <= width) {
                markerBottom.draw(canvas, mViewPortHandler.contentLeft() + markerBottom.getWidth() / 2, mViewPortHandler.contentBottom() + markerBottom.getHeight());
            } else {
                markerBottom.draw(canvas, pos[0], mViewPortHandler.contentBottom() + markerBottom.getHeight());
            }
        }
    }
}
