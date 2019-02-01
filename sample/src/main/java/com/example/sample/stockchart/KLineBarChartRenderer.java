package com.example.sample.stockchart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author weixia
 * @date 2019/1/15.
 */
@SuppressWarnings("WeakerAccess")
public class KLineBarChartRenderer extends BarChartRenderer {

    public KLineBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0f;
        final float phaseX = mAnimator.getPhaseX();
        final float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());
            final BarData barData = mChart.getBarData();
            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)),
                    dataSet.getEntryCount()); i < count; i++) {

                final BarEntry e = dataSet.getEntryForIndex(i);
                x = e.getX() + offSet;
                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;
                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    continue;
                }
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) {
                    break;
                }

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();
                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        final BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());
        buffer.feed(dataSet);
        trans.pointValuesToPixel(buffer.buffer);

        for (int j = 0; j < buffer.size(); j += 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                continue;
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break;
            }

            final int i = j / 4;
            final Object openClose = dataSet.getEntryForIndex(i).getData();
            if (openClose != null) {
                //用于K线图
                //根据开平判断柱状图的颜色填充
                final float value = (Float) openClose;
                if (value > 0) {//表示增加
                    increasingSet(dataSet, j);
                } else if (value <= 0) {
                    decreasingSet(dataSet, j);
                }
            }

            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    private void increasingSet(IBarDataSet dataSet, int index) {
        mRenderPaint.setColor(dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                dataSet.getColor(index) :
                dataSet.getIncreasingColor());
        mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());
    }

    private void neutralSet(IBarDataSet dataSet, int index) {
        mRenderPaint.setColor(dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
                dataSet.getColor(index) :
                dataSet.getNeutralColor());
        mRenderPaint.setStyle(dataSet.getNeutralPaintStyle());
    }

    private void decreasingSet(IBarDataSet dataSet, int index) {
        mRenderPaint.setColor(dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                dataSet.getColor(index) :
                dataSet.getDecreasingColor());
        mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());
    }
}
