package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * Bubble chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed
 * under Apache License 2.0 Ported by Daniel Cohen Gindi
 */
@SuppressWarnings("WeakerAccess")
public class BubbleChartRenderer extends BarLineScatterCandleBubbleRenderer {

    protected BubbleDataProvider mChart;

    public BubbleChartRenderer(BubbleDataProvider chart, ChartAnimator animator,
                               ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mRenderPaint.setStyle(Style.FILL);

        mHighlightPaint.setStyle(Style.STROKE);
        mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(1.5f));
    }

    @Override
    public void initBuffers() {
    }

    @Override
    public void drawData(Canvas c) {
        final BubbleData bubbleData = mChart.getBubbleData();
        for (IBubbleDataSet set : bubbleData.getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }
    }

    private float[] sizeBuffer = new float[4];
    private float[] pointBuffer = new float[2];

    protected float getShapeSize(float entrySize, float maxSize, float reference, boolean normalizeSize) {
        final float factor = normalizeSize ? ((maxSize == 0f) ? 1f : (float) Math.sqrt(entrySize / maxSize)) : entrySize;
        return reference * factor;
    }

    protected void drawDataSet(Canvas c, IBubbleDataSet dataSet) {
        if (dataSet.getEntryCount() < 1) {
            return;
        }

        final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        final float phaseY = mAnimator.getPhaseY();

        mXBounds.set(mChart, dataSet);

        sizeBuffer[0] = 0f;
        sizeBuffer[2] = 1f;

        trans.pointValuesToPixel(sizeBuffer);

        final boolean normalizeSize = dataSet.isNormalizeSizeEnabled();

        // calcualte the full width of 1 step on the x-axis
        final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
        final float maxBubbleHeight = Math.abs(mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
        final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {
            final BubbleEntry entry = dataSet.getEntryForIndex(j);
            pointBuffer[0] = entry.getX();
            pointBuffer[1] = (entry.getY()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            final float shapeHalf = getShapeSize(entry.getSize(), dataSet.getMaxSize(), referenceSize, normalizeSize) / 2f;
            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf)) {
                continue;
            }
            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf)) {
                continue;
            }
            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf)) {
                break;
            }
            final int color = dataSet.getColor((int) entry.getX());
            mRenderPaint.setColor(color);
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, mRenderPaint);
        }
    }

    @Override
    public void drawValues(Canvas c) {
        final BubbleData bubbleData = mChart.getBubbleData();
        if (bubbleData == null) {
            return;
        }

        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {
            final List<IBubbleDataSet> dataSets = bubbleData.getDataSets();
            final float lineHeight = Utils.calcTextHeight(mValuePaint, "1");

            for (int i = 0; i < dataSets.size(); i++) {
                final IBubbleDataSet dataSet = dataSets.get(i);
                if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1) {
                    continue;
                }

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                final float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
                final float phaseY = mAnimator.getPhaseY();

                mXBounds.set(mChart, dataSet);

                final float[] positions = mChart.getTransformer(dataSet.getAxisDependency())
                        .generateTransformedValuesBubble(dataSet, phaseY, mXBounds.min, mXBounds.max);

                final float alpha = phaseX == 1 ? phaseY : phaseX;
                final ValueFormatter formatter = dataSet.getValueFormatter();
                final MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                for (int j = 0; j < positions.length; j += 2) {
                    int valueTextColor = dataSet.getValueTextColor(j / 2 + mXBounds.min);
                    valueTextColor = Color.argb(Math.round(255.f * alpha), Color.red(valueTextColor),
                            Color.green(valueTextColor), Color.blue(valueTextColor));

                    final float x = positions[j];
                    final float y = positions[j + 1];
                    if (!mViewPortHandler.isInBoundsRight(x)) {
                        break;
                    }
                    if ((!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))) {
                        continue;
                    }
                    final BubbleEntry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);
                    if (dataSet.isDrawValuesEnabled()) {
                        drawValue(c, formatter.getBubbleLabel(entry), x, y + (0.5f * lineHeight), valueTextColor);
                    }
                    if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                        final Drawable icon = entry.getIcon();
                        Utils.drawImage(
                                c,
                                icon,
                                (int) (x + iconsOffset.x),
                                (int) (y + iconsOffset.y),
                                icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight());
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    private float[] _hsvBuffer = new float[3];

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        final BubbleData bubbleData = mChart.getBubbleData();
        final float phaseY = mAnimator.getPhaseY();

        for (Highlight high : indices) {
            final IBubbleDataSet set = bubbleData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled()) {
                continue;
            }

            final BubbleEntry entry = set.getEntryForXValue(high.getX(), high.getY());
            if (entry.getY() != high.getY()) {
                continue;
            }
            if (!isInBoundsX(entry, set)) {
                continue;
            }

            final Transformer trans = mChart.getTransformer(set.getAxisDependency());
            sizeBuffer[0] = 0f;
            sizeBuffer[2] = 1f;

            trans.pointValuesToPixel(sizeBuffer);

            final boolean normalizeSize = set.isNormalizeSizeEnabled();

            // calcualte the full width of 1 step on the x-axis
            final float maxBubbleWidth = Math.abs(sizeBuffer[2] - sizeBuffer[0]);
            final float maxBubbleHeight = Math.abs(
                    mViewPortHandler.contentBottom() - mViewPortHandler.contentTop());
            final float referenceSize = Math.min(maxBubbleHeight, maxBubbleWidth);

            pointBuffer[0] = entry.getX();
            pointBuffer[1] = (entry.getY()) * phaseY;
            trans.pointValuesToPixel(pointBuffer);

            high.setDraw(pointBuffer[0], pointBuffer[1]);

            final float shapeHalf = getShapeSize(entry.getSize(),
                    set.getMaxSize(),
                    referenceSize,
                    normalizeSize) / 2f;

            if (!mViewPortHandler.isInBoundsTop(pointBuffer[1] + shapeHalf)
                    || !mViewPortHandler.isInBoundsBottom(pointBuffer[1] - shapeHalf)) {
                continue;
            }
            if (!mViewPortHandler.isInBoundsLeft(pointBuffer[0] + shapeHalf)) {
                continue;
            }
            if (!mViewPortHandler.isInBoundsRight(pointBuffer[0] - shapeHalf)) {
                break;
            }

            final int originalColor = set.getColor((int) entry.getX());
            Color.RGBToHSV(Color.red(originalColor), Color.green(originalColor),
                    Color.blue(originalColor), _hsvBuffer);
            _hsvBuffer[2] *= 0.5f;
            final int color = Color.HSVToColor(Color.alpha(originalColor), _hsvBuffer);

            mHighlightPaint.setColor(color);
            mHighlightPaint.setStrokeWidth(set.getHighlightCircleWidth());
            c.drawCircle(pointBuffer[0], pointBuffer[1], shapeHalf, mHighlightPaint);
        }
    }
}
