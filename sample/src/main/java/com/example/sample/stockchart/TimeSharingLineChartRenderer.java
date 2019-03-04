package com.example.sample.stockchart;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingLineChartRenderer extends LineChartRenderer {

    public TimeSharingLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    /**
     * Draws a normal line.
     */
    @Override
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {
        final int entryCount = dataSet.getEntryCount();
        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();
        final int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;

        final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas;
        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        mXBounds.set(mChart, dataSet);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entryCount > 0) {
            drawLinearFill(c, dataSet, trans, mXBounds);
        }

        // more than 1 color
        if (dataSet.getColors().size() > 1) {
            if (mLineBuffer.length <= pointsPerEntryPair * 2) {
                mLineBuffer = new float[pointsPerEntryPair * 4];
            }

            for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {
                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) {
                    continue;
                }

                mLineBuffer[0] = e.getX() + mOffSet;
                mLineBuffer[1] = e.getY() * phaseY;

                if (j < mXBounds.max) {
                    e = dataSet.getEntryForIndex(j + 1);
                    if (e == null) {
                        break;
                    }

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getX() + mOffSet;
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getX() + mOffSet;
                        mLineBuffer[7] = e.getY() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getX() + mOffSet;
                        mLineBuffer[3] = e.getY() * phaseY;
                    }
                } else {
                    mLineBuffer[2] = mLineBuffer[0];
                    mLineBuffer[3] = mLineBuffer[1];
                }

                trans.pointValuesToPixel(mLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mLineBuffer[0])) {
                    break;
                }

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(mLineBuffer[2])
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3]))) {
                    continue;
                }

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j));

                canvas.drawLines(mLineBuffer, 0, pointsPerEntryPair * 2, mRenderPaint);
            }

        } else { // only one color per dataset
            if (mLineBuffer.length < Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 2) {
                mLineBuffer = new float[Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 4];
            }

            Entry e1, e2;
            e1 = dataSet.getEntryForIndex(mXBounds.min);
            if (e1 != null) {
                int j = 0;
                for (int x = mXBounds.min; x <= mXBounds.range + mXBounds.min; x++) {
                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);
                    if (e1 == null || e2 == null) {
                        continue;
                    }

                    mLineBuffer[j++] = e1.getX() + mOffSet;
                    mLineBuffer[j++] = e1.getY() * phaseY;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[j++] = e2.getX() + mOffSet;
                        mLineBuffer[j++] = e1.getY() * phaseY;
                        mLineBuffer[j++] = e2.getX() + mOffSet;
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    }
                    //这些点与点之间不连接，用于五日分时
                    if (dataSet.getTimeDayType() == 5 && dataSet.getXLabels().indexOfKey(x == 0 ? 0 : (x - 1)) > 0) {
                        mLineBuffer[j++] = e1.getX() + mOffSet;
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    } else {
                        mLineBuffer[j++] = e2.getX() + mOffSet;
                        mLineBuffer[j++] = e2.getY() * phaseY;
                    }
                }

                if (j > 0) {
                    trans.pointValuesToPixel(mLineBuffer);
                    final int size = Math.max((mXBounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2;

                    mRenderPaint.setColor(dataSet.getColor());
                    canvas.drawLines(mLineBuffer, 0, size, mRenderPaint);
                }
            }
        }

        mRenderPaint.setPathEffect(null);
    }
}
