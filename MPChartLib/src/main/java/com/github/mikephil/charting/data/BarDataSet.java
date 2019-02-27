package com.github.mikephil.charting.data;

import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BarDataSet extends BarLineScatterCandleBubbleDataSet<BarEntry> implements IBarDataSet {

    /**
     * the maximum number of bars that are stacked upon each other, this value
     * is calculated from the Entries that are added to the DataSet
     */
    private int mStackSize = 1;

    /**
     * the color used for drawing the bar shadows
     */
    private int mBarShadowColor = Color.rgb(215, 215, 215);

    private float mBarBorderWidth = 0.0f;

    private int mBarBorderColor = Color.BLACK;

    /**
     * the alpha value used to draw the highlight indicator bar
     */
    private int mHighLightAlpha = 120;

    /**
     * the overall entry count, including counting each stack-value individually
     */
    private int mEntryCountStacks = 0;
    /**
     * paint style when open < close
     * increasing candlesticks are traditionally hollow
     */
    private Paint.Style mIncreasingPaintStyle = Paint.Style.STROKE;

    /**
     * paint style when open > close
     * descreasing candlesticks are traditionally filled
     */
    private Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;

    /**
     * paint style when open = close
     * descreasing candlesticks are traditionally filled
     */
    private Paint.Style mNeutralPaintStyle = Paint.Style.FILL;

    /**
     * color for open == close
     */
    private int mNeutralColor = ColorTemplate.COLOR_NONE;

    /**
     * color for open < close
     */
    private int mIncreasingColor = ColorTemplate.COLOR_NONE;

    /**
     * color for open > close
     */
    private int mDecreasingColor = ColorTemplate.COLOR_NONE;
    /**
     * array of labels used to describe the different values of the stacked bars
     */
    private String[] mStackLabels = new String[]{"Stack"};

    public BarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);

        mHighLightColor = Color.rgb(0, 0, 0);

        calcStackSize(yVals);
        calcEntryCountIncludingStacks(yVals);
    }

    @Override
    public DataSet<BarEntry> copy() {
        final List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < mValues.size(); i++) {
            entries.add(mValues.get(i).copy());
        }
        final BarDataSet copied = new BarDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(BarDataSet barDataSet) {
        super.copy(barDataSet);
        barDataSet.mStackSize = mStackSize;
        barDataSet.mBarShadowColor = mBarShadowColor;
        barDataSet.mBarBorderWidth = mBarBorderWidth;
        barDataSet.mStackLabels = mStackLabels;
        barDataSet.mHighLightAlpha = mHighLightAlpha;
    }

    /**
     * Calculates the total number of entries this DataSet represents, including
     * stacks. All values belonging to a stack are calculated separately.
     */
    private void calcEntryCountIncludingStacks(List<BarEntry> yVals) {
        mEntryCountStacks = 0;

        for (int i = 0; i < yVals.size(); i++) {
            final float[] vals = yVals.get(i).getYVals();
            if (vals == null) {
                mEntryCountStacks++;
            } else {
                mEntryCountStacks += vals.length;
            }
        }
    }

    /**
     * calculates the maximum stacksize that occurs in the Entries array of this
     * DataSet
     */
    private void calcStackSize(List<BarEntry> yVals) {
        for (int i = 0; i < yVals.size(); i++) {
            final float[] vals = yVals.get(i).getYVals();
            if (vals != null && vals.length > mStackSize) {
                mStackSize = vals.length;
            }
        }
    }

    @Override
    protected void calcMinMax(BarEntry e) {
        if (e != null && !Float.isNaN(e.getY())) {
            if (e.getYVals() == null) {
                if (e.getY() < mYMin) {
                    mYMin = e.getY();
                }
                if (e.getY() > mYMax) {
                    mYMax = e.getY();
                }
            } else {
                if (-e.getNegativeSum() < mYMin) {
                    mYMin = -e.getNegativeSum();
                }
                if (e.getPositiveSum() > mYMax) {
                    mYMax = e.getPositiveSum();
                }
            }

            calcMinMaxX(e);
        }
    }

    @Override
    public int getStackSize() {
        return mStackSize;
    }

    @Override
    public boolean isStacked() {
        return mStackSize > 1;
    }

    /**
     * returns the overall entry count, including counting each stack-value
     * individually
     */
    public int getEntryCountStacks() {
        return mEntryCountStacks;
    }

    /**
     * Sets the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value. Don't for get to
     * use getResources().getColor(...) to set this. Or Color.rgb(...).
     */
    public void setBarShadowColor(int color) {
        mBarShadowColor = color;
    }

    @Override
    public int getBarShadowColor() {
        return mBarShadowColor;
    }

    /**
     * Sets the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     */
    public void setBarBorderWidth(float width) {
        mBarBorderWidth = width;
    }

    /**
     * Returns the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     */
    @Override
    public float getBarBorderWidth() {
        return mBarBorderWidth;
    }

    /**
     * Sets the color drawing borders around the bars.
     */
    public void setBarBorderColor(int color) {
        mBarBorderColor = color;
    }

    /**
     * Returns the color drawing borders around the bars.
     */
    @Override
    public int getBarBorderColor() {
        return mBarBorderColor;
    }

    /**
     * Set the alpha value (transparency) that is used for drawing the highlight
     * indicator bar. min = 0 (fully transparent), max = 255 (fully opaque)
     */
    public void setHighLightAlpha(int alpha) {
        mHighLightAlpha = alpha;
    }

    @Override
    public int getHighLightAlpha() {
        return mHighLightAlpha;
    }

    /**
     * Sets labels for different values of bar-stacks, in case there are one.
     */
    public void setStackLabels(String[] labels) {
        mStackLabels = labels;
    }

    @Override
    public String[] getStackLabels() {
        return mStackLabels;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open == close.
     */
    public void setNeutralColor(int color) {
        mNeutralColor = color;
    }

    @Override
    public int getNeutralColor() {
        return mNeutralColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open <= close.
     */
    public void setIncreasingColor(int color) {
        mIncreasingColor = color;
    }

    @Override
    public int getIncreasingColor() {
        return mIncreasingColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open > close.
     */
    public void setDecreasingColor(int color) {
        mDecreasingColor = color;
    }

    @Override
    public int getDecreasingColor() {
        return mDecreasingColor;
    }

    @Override
    public Paint.Style getIncreasingPaintStyle() {
        return mIncreasingPaintStyle;
    }

    /**
     * Sets paint style when open < close
     */
    public void setIncreasingPaintStyle(Paint.Style paintStyle) {
        this.mIncreasingPaintStyle = paintStyle;
    }

    @Override
    public Paint.Style getDecreasingPaintStyle() {
        return mDecreasingPaintStyle;
    }

    /**
     * Sets paint style when open > close
     */
    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    @Override
    public Paint.Style getNeutralPaintStyle() {
        return mNeutralPaintStyle;
    }

    public void setNeutralPaintStyle(Paint.Style neutralPaintStyle) {
        mNeutralPaintStyle = neutralPaintStyle;
    }
}
